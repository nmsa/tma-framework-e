package eubr.atmosphere.tma.actuator;

public class DataTransferObject {
    
    private String myAttribute;
    
    public DataTransferObject() { }
    
    public DataTransferObject(String myAttribute) {
        this.myAttribute = myAttribute;
    }

    public String getMyAttribute() {
        return myAttribute;
    }

    public void setMyAttribute(String myAttribute) {
        this.myAttribute = myAttribute;
    }
}
