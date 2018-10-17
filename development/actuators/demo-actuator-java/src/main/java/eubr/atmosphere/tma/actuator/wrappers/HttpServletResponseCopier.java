package eubr.atmosphere.tma.actuator.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServletResponseCopier extends HttpServletResponseWrapper {

    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ServletOutputStreamCopier copier;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletResponseCopier.class);

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private PrintWriter pw = null;

    private ServletOutputStream sos = null;

    public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
        super(response);
    }
    
    public byte[] getByteArray()
    {
        return baos.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (sos == null) {
            /*outputStream = getResponse().getOutputStream();
            LOGGER.info(" PEGOU O OUTPUTSTREAM ");
            copier = new ServletOutputStreamCopier(outputStream);*/
            sos = new ByteArrayServletStream(baos);
            copier = new ServletOutputStreamCopier(sos);
        }

        return sos;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (pw == null) {
            pw = new PrintWriter(baos);
            copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
        }

        return pw;
    }

    /*@Override
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
    }*/

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
