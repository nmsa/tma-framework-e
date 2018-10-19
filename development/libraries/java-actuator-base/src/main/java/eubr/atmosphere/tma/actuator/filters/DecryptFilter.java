package eubr.atmosphere.tma.actuator.filters;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Base64;

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

import eubr.atmosphere.tma.actuator.crypto.KeyManager;
import eubr.atmosphere.tma.actuator.utils.PropertiesManager;
import eubr.atmosphere.tma.actuator.wrappers.HttpServletRequestWritableWrapper;
import eubr.atmosphere.tma.actuator.wrappers.HttpServletResponseCopier;

@WebFilter(filterName = "decryptFilter")
@Component
public class DecryptFilter implements Filter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        LOGGER.info("Logging Request {} : {}", req.getMethod(), req.getRequestURI());

        // TODO: Handle cases where the key is not valid
        String privateKeyPath = PropertiesManager.getInstance().getProperty("privateKeyActuatorPath");
        PrivateKey privateKey = KeyManager.getPrivateKey(privateKeyPath);
        byte[] bodyBytes = KeyManager.decrypt(getBody(req), privateKey);
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

        byte[] signedResponseByteArray = KeyManager.sign(plainResponse, privateKey);
        String signedResponse = Base64.getEncoder().encodeToString(signedResponseByteArray);
        LOGGER.info("signedResponse: " + signedResponse);

        String publicKeyExecutorPath = PropertiesManager.getInstance().getProperty("publicKeyExecutorPath");
        PublicKey publicKeyExecutor = KeyManager.getPublicKey(publicKeyExecutorPath);
        servletResponse.getWriter().write(Base64.getEncoder().encodeToString(KeyManager.encrypt(plainResponse.getBytes(), publicKeyExecutor)));
        servletResponse.getWriter().write("\n");
        servletResponse.getWriter().write(signedResponse);
    }

    @Override
    public void destroy() { }

    private byte[] getBody(HttpServletRequest request) {
        try {
            byte[] body = IOUtils.toByteArray(request.getInputStream());
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}