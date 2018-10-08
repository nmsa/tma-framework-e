package eubr.atmosphere.tma.actuator.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eubr.atmosphere.tma.actuator.wrappers.HttpServletRequestWritableWrapper;
import eubr.atmosphere.tma.actuator.wrappers.HttpServletResponseReadableWrapper;

@WebFilter(filterName = "decryptFilter", urlPatterns = {"/securePOC*"})
@Component
public class DecryptFilter implements Filter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptFilter.class);

    public static final String ALGORITHM = "RSA";

    // This is the current test:
    // curl --header "Content-Type: text/plain" --request POST --data-binary "@encrypted-message" http://localhost:8080/securePOC/act

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("MyFilter.doFilter");
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        LOGGER.info("Logging Request {} : {}", req.getMethod(), req.getRequestURI());

        PrivateKey privateKey = getPrivateKey();

        byte[] bodyBytes = decrypt(getBody(req), privateKey);
        String decryptedData = new String(bodyBytes);
        LOGGER.info(decryptedData);

        HttpServletRequestWritableWrapper requestWrapper =
                new HttpServletRequestWritableWrapper(
                        (HttpServletRequest) request, bodyBytes);

            HttpServletResponseReadableWrapper responseWrapper
                = new HttpServletResponseReadableWrapper((HttpServletResponse) response);

        chain.doFilter(requestWrapper, responseWrapper);
        LOGGER.info( "Logging Response :{}", res.getContentType());

        /*String encryptedData = encrypt(responseWrapper, ...);
        response.getWriter().write(encryptedData);*/
    }

    private PrivateKey getPrivateKey() {
        String filenamePrivKey = "/home/virt-atm/Documents/priv-key-again";

        try {
            File privKeyFile = new File(filenamePrivKey);

            // read private key DER file
            DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
            byte[] privKeyBytes = new byte[(int)privKeyFile.length()];
            dis.read(privKeyBytes);
            dis.close();

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            // decode private key
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
            RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

            return privKey;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private PublicKey getPublicKey() {
        String filenamePubKey = "/home/virt-atm/Documents/pub-key-again";

        try {
            File pubKeyFile = new File(filenamePubKey);

            DataInputStream dis = new DataInputStream(new FileInputStream(pubKeyFile));
            byte[] pubKeyBytes = new byte[(int)pubKeyFile.length()];
            dis.readFully(pubKeyBytes);
            dis.close();

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            // decode public key
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubKeyBytes);
            RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

            return pubKey;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void destroy() { }

    private byte[] getBody(HttpServletRequest request) {
        try {
            byte[] body = IOUtils.toByteArray(request.getInputStream());
            LOGGER.info( "This is the body! : {}", body);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

         } catch (Exception ex) {
             ex.printStackTrace();
         }
         return dectyptedText;
    }

    public class GenericRequestWrapper extends HttpServletRequestWrapper {

        HttpServletRequest origRequest;
        byte[] reqBytes;
        boolean firstTime = true;

        /**
        * @param arg0
        */
        public GenericRequestWrapper(HttpServletRequest arg0) {
            super(arg0);
            origRequest = arg0;
            // TODO Auto-generated constructor stub
        }

        public BufferedReader getReader() throws IOException {

            if (firstTime) {
                firstTime = false;
                StringBuffer sbuf = new StringBuffer();
                BufferedReader oreader = origRequest.getReader();
                String line;
                while((line = oreader.readLine()) != null) {
                    sbuf.append(line);
                    sbuf.append("\n\r");
                }
                reqBytes = sbuf.toString().getBytes();
            }

            InputStreamReader dave = new InputStreamReader(new ByteArrayInputStream(reqBytes));
            BufferedReader br = new BufferedReader(dave);
            return br;
        }
    }
}
