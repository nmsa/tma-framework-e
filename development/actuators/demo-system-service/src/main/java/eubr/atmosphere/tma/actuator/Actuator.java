package eubr.atmosphere.tma.actuator;

import java.util.Map;

public interface Actuator {

	public void act (int resourceId, String action, Map<String, Object> config);
}
