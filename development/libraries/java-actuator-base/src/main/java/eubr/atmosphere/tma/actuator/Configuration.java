package eubr.atmosphere.tma.actuator;

public class Configuration {

    private String keyName;
    private String value;

    public Configuration() { }

    public Configuration(String keyName, String value) {
        this.keyName = keyName;
        this.value = value;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Configuration [keyName=" + keyName + ", value=" + value + "]";
    }
}
