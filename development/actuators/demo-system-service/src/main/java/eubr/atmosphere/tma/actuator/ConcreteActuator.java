package eubr.atmosphere.tma.actuator;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component("actuator")
public class ConcreteActuator implements Actuator {

	@Override
	public void act(int resourceId, String action, Map<String, Object> config) {
		// This is the actuation itself.
		// TODO Decide what action to perform, and perform it
		
		switch (action) {
		case "action1":
			System.out.println("Action 1" + action);
			break;
			
		case "action2":
			System.out.println("Action 2" + action);
			break;

		default:
			System.out.println("Not defined action: " + action);
			break;
		}

	}

}
