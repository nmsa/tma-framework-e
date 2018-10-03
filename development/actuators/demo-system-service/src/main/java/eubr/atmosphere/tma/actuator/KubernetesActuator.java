package eubr.atmosphere.tma.actuator;

import java.io.IOException;
import java.util.HashMap;
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
            
            scalePods(actuatorPayload.getResourceId(),
                    actuatorPayload.getAction(), actuatorPayload.getConfiguration());
            break;

        default:
            System.out.println("Not defined action");
            break;
        }
    }

    private void scalePods(int resourceId, String action, Map<String, String> config) {
        System.out.println("scale: " + action);
        System.out.println("resourceId: " + resourceId);
        System.out.println(config);

        Map<String, Object> configuration = new HashMap<String, Object>();
        configuration.put("metadata.namespace", config.get("metadata.namespace"));
        configuration.put("metadata.name", config.get("metadata.name"));
        configuration.put("spec.replicas", config.get("spec.replicas"));
        try {
            RestServices.requestPutRestService(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
