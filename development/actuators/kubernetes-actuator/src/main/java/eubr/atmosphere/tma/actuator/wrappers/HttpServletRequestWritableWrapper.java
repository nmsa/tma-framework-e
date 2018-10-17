package eubr.atmosphere.tma.actuator.wrappers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

public class HttpServletRequestWritableWrapper extends HttpServletRequestWrapper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletRequestWrapper.class);

    private final ByteArrayInputStream decryptedDataBAIS;

    public HttpServletRequestWritableWrapper(HttpServletRequest request, byte[] decryptedData) {
        super(request);
        decryptedDataBAIS = new ByteArrayInputStream(decryptedData);
    }

    @Override
    public String getHeader(String headerName) {
        String headerValue = super.getHeader(headerName);
        if ("Accept".equalsIgnoreCase(headerName)) {
            return headerValue.replaceAll(
                MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE);
        } else if ("Content-Type".equalsIgnoreCase(headerName)) {
            return headerValue.replaceAll(
                MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE);
        }
        return headerValue;
    }

    @Override
    public Enumeration getHeaders(final String headerName) {
        List<String> headerVals = Collections.list(super.getHeaders(headerName));
        int index = 0;
        for (String value : headerVals) {
            if ("Content-Type".equalsIgnoreCase(headerName)) {
                LOGGER.debug("Content type change: ");
                headerVals.set(index, MediaType.APPLICATION_JSON_VALUE);
            }
            index++;
        }

        return Collections.enumeration(headerVals);
    }

    @Override
    public String getContentType() {
        String contentTypeValue = super.getContentType();
        if (MediaType.TEXT_PLAIN_VALUE.equalsIgnoreCase(contentTypeValue)) {
            return MediaType.APPLICATION_JSON_VALUE;
        }
        return contentTypeValue;
    }

    @Override
    public BufferedReader getReader() throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(decryptedDataBAIS, "UTF_8"));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            
            @Override
            public int read() throws IOException {
                // TODO Auto-generated method stub
                return decryptedDataBAIS.read();
            }
            
            @Override
            public void setReadListener(ReadListener listener) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public boolean isReady() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isFinished() {
                // TODO Auto-generated method stub
                return false;
            }
        };
    }
}
