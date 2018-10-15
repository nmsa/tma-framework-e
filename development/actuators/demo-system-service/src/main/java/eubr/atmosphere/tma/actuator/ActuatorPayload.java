package eubr.atmosphere.tma.actuator;

import java.util.List;

import eubr.atmosphere.tma.actuator.examples.Configuration;

public class ActuatorPayload {
    private String action;
    private int resourceId;
    private int messageId;
    private Long timestamp;
    private List<Configuration> configuration;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

	public List<Configuration> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(List<Configuration> configuration) {
		this.configuration = configuration;
	}

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
