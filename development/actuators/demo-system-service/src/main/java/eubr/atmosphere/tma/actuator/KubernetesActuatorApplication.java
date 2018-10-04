package eubr.atmosphere.tma.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KubernetesActuatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesActuatorApplication.class, args);
	}
}
