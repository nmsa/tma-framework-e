package eubr.atmosphere.tma.actuator;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eubr.atmosphere.tma.actuator.services.RestServices;

@RestController
@RequestMapping("/k8sActuator")
public class KubernetesActuator implements Actuator {

    @Override
    @PostMapping(path = "/act")
    public void act(@RequestBody ActuatorPayload actuatorPayload) {
        switch (actuatorPayload.getAction()) {
        case "scale":
            
            scalePods(actuatorPayload);
            break;

        default:
            System.out.println("Not defined action");
            break;
        }
    }

    private void scalePods(ActuatorPayload actuatorPayload) {
        System.out.println("action: " + actuatorPayload.getAction());
        System.out.println("resourceId: " + actuatorPayload.getResourceId());
        System.out.println("messageId: " + actuatorPayload.getMessageId());
        System.out.println("timestamp: " + actuatorPayload.getTimestamp());
        Map<String, String> config = actuatorPayload.getConfiguration();
        System.out.println(config);

        try {
            RestServices.requestPutRestService(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
