package eubr.atmosphere.tma.actuator.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ByteArrayServletStream extends ServletOutputStream { 
    
    ByteArrayOutputStream baos; 
 
    ByteArrayServletStream(ByteArrayOutputStream baos) { 
        this.baos = baos; 
    } 
 
    @Override 
    public void write(int param) throws IOException { 
        baos.write(param); 
    }

    @Override
    public boolean isReady() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        // TODO Auto-generated method stub
        
    } 
}
