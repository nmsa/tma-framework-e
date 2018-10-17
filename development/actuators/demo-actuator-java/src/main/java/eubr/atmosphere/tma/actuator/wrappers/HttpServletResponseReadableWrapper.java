package eubr.atmosphere.tma.actuator.wrappers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServletResponseReadableWrapper extends HttpServletResponseWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletResponseReadableWrapper.class);

    public HttpServletResponseReadableWrapper(HttpServletResponse response) {
        super(response);
    }

//    private StringWriter sw = new StringWriter(/*BUFFER_SIZE*/);
//
//    public PrintWriter getWriter() throws IOException {
//      return new PrintWriter(sw);
//    }
//
//   /*public ServletOutputStream getOutputStream() throws IOException {
//      throw new UnsupportedOperationException();
//    }*/
//
//    @Override
//    public String getHeader(String headerName) {
//        String headerValue = super.getHeader(headerName);
//        if ("Accept".equalsIgnoreCase(headerName)) {
//            return headerValue.replaceAll(
//                MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE);
//        } else if ("Content-Type".equalsIgnoreCase(headerName)) {
//            return headerValue.replaceAll(
//                MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE);
//        }
//        return headerValue;
//    }
//
//    @Override
//    public Collection<String> getHeaders(final String headerName) {
//        List<String> headerVals = new ArrayList<String>(super.getHeaders(headerName));
//        int index = 0;
//        for (String value : headerVals) {
//            if ("Content-Type".equalsIgnoreCase(headerName)) {
//                LOGGER.debug("Content type change: ");
//                headerVals.set(index, MediaType.TEXT_PLAIN_VALUE);
//            }
//            index++;
//        }
//
//        return headerVals;
//    }
//
//    public String toString() {
//      return sw.toString();
//    }

    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ServletOutputStreamCopier copier;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copier = new ServletOutputStreamCopier(outputStream);
        }

        return copier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copier.flush();
        }
    }

    public byte[] getCopy() {
        if (copier != null) {
            return copier.getCopy();
        } else {
            return new byte[0];
        }
    }
}
