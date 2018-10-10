package eubr.atmosphere.tma.actuator.filters;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eubr.atmosphere.tma.actuator.crypto.SymmetricKeyManager;
import eubr.atmosphere.tma.actuator.wrappers.HttpServletRequestWritableWrapper;
import eubr.atmosphere.tma.actuator.wrappers.HttpServletResponseCopier;

@WebFilter(filterName = "decryptFilter", urlPatterns = {"/securePOC*"})
@Component
public class DecryptFilter implements Filter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptFilter.class);

    public static final String ALGORITHM = "RSA";
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    public static final String SECRET = "!@#$MySecr3tPasSw0rd";
    public static final String SYMMETRIC_KEY_ALGORITHM = "AES";

    // This is the current test:
    // curl --header "Content-Type: text/plain" --request POST --data-binary "@encrypted-message" http://localhost:8080/securePOC/act

    // Reference: https://www.taringamberini.com/en/blog/java/adding-encryption-to-a-restful-web-service/

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        LOGGER.info("Logging Request {} : {}", req.getMethod(), req.getRequestURI());

        PrivateKey privateKey = getPrivateKey("/home/virt-atm/Documents/priv-key-again");
        byte[] bodyBytes = decrypt(getBody(req), privateKey);
        String decryptedData = new String(bodyBytes);
        LOGGER.info(decryptedData);

        HttpServletRequestWritableWrapper requestWrapper =
                new HttpServletRequestWritableWrapper(
                        (HttpServletRequest) servletRequest, bodyBytes);
        HttpServletResponseCopier responseCopier =
                new HttpServletResponseCopier((HttpServletResponse) servletResponse);

        try {
            chain.doFilter(requestWrapper, responseCopier);
            responseCopier.flushBuffer();
        } finally {
            byte[] copy = responseCopier.getByteArray();
            String plainResponse = new String(copy, servletResponse.getCharacterEncoding());

            try {
                encryptResponse(servletResponse, privateKey, plainResponse);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            }
        }
    }

    private void encryptResponse(ServletResponse servletResponse, PrivateKey privateKey,
            String plainResponse) throws InvalidKeyException, IOException, NoSuchAlgorithmException, SignatureException {

        PublicKey publicKeyActuator = getPublicKey("/home/virt-atm/Documents/pub-key-again");

        byte[] signedResponseByteArray = sign(plainResponse, privateKey);
        String signedResponse = Base64.getEncoder().encodeToString(signedResponseByteArray);
        LOGGER.info("signedResponse: " + signedResponse);

        byte[] encryptedData = getEncryptedData(signedResponse);
        byte[] decryptedDataByteArray = getDecryptedData(encryptedData);

        LOGGER.info("MAIS UM TESTE: {}", verify(plainResponse, new String(decryptedDataByteArray), publicKeyActuator));
        LOGGER.info("encryptedData: {}", new String(encryptedData));
        LOGGER.info("decryptedDataByteArray: " + new String(decryptedDataByteArray));

        LOGGER.info("encryptedData.length: {}", encryptedData.length);
        LOGGER.info("(new String(encryptedData)).length(): {}", (new String(encryptedData)).length());

        PublicKey publicKeyExecutor = getPublicKey("/home/virt-atm/Documents/pub-key-execute");
        servletResponse.getWriter().write(Base64.getEncoder().encodeToString(encrypt(plainResponse.getBytes(), publicKeyExecutor)));
        servletResponse.getWriter().write("\n");
        servletResponse.getWriter().write(signedResponse);
    }

    public byte[] getEncryptedData(String message) {
        SymmetricKeyManager skm;
        byte[] output = null;
        try {
            skm = new SymmetricKeyManager(SECRET, 16, SYMMETRIC_KEY_ALGORITHM);
            output = skm.encrypt(message);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return output;
    }

    public byte[] getDecryptedData(byte[] message) {
        SymmetricKeyManager skm;
        byte[] output = null;
        try {
            skm = new SymmetricKeyManager(SECRET, 16, "AES");
            output = skm.decrypt(message);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return output;
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public byte[] sign(String data, PrivateKey keyFile)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
        privateSignature.initSign(keyFile);
        privateSignature.update(data.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();
        return signature;
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

    private PrivateKey getPrivateKey(String filenamePrivKey) {
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private PublicKey getPublicKey(String filenamePubKey) {
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
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

    public static byte[] encrypt(byte[] text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
          } catch (Exception e) {
            e.printStackTrace();
          }
          return cipherText;
   }
}