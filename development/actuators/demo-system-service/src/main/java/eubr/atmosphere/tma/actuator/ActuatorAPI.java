package eubr.atmosphere.tma.actuator;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ActuatorAPI")
public class ActuatorAPI {
	
	@RequestMapping("/initialize")
	public void initialize() {
		// TODO: this will initialize the API
		System.out.println("Initialize :)");
	}
	
	@RequestMapping("/act")
	public void act(/*EncryptedMessage*/String action) {
		// TODO: the message should be encrypted
		
		// A possible implementation of the encrypt/decrypt message: 
		// https://www.taringamberini.com/en/blog/java/adding-encryption-to-a-restful-web-service/
		
		System.out.println("Act new!! " +  action);
	}
	
	@RequestMapping("/register")
	protected void register(Actuator callback) {
		// TODO: it will register the Actuator to the API
		// TODO: This should not be a service. It should
	}
}
