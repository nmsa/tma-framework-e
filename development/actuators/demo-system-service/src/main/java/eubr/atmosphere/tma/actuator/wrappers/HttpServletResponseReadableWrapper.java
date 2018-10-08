package eubr.atmosphere.tma.actuator.wrappers;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpServletResponseReadableWrapper extends HttpServletResponseWrapper {

    public HttpServletResponseReadableWrapper(HttpServletResponse response) {
        super(response);
    }
}
