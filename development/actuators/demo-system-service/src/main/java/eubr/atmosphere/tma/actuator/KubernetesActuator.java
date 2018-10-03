package eubr.atmosphere.tma.actuator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eubr.atmosphere.tma.actuator.services.RestServices;

@RestController
@RequestMapping("/k8sActuator")
public class KubernetesActuator implements Actuator {

    @Override
    @RequestMapping("/act")
    public void act(int resourceId, String action, @RequestParam Map<String, Object> config) {
        switch (action) {
        case "scale":
            scalePods(resourceId, action, config);
            break;
            
        default:
            System.out.println("Not defined action: " + action);
            break;
        }
    }

    private void scalePods(int resourceId, String action, Map<String, Object> config) {
        System.out.println("scale: " + action);
        System.out.println("resourceId: " + resourceId);
        System.out.println(config);

        Map<String, Object> configuration = new HashMap<String, Object>();
        configuration.put("metadata.namespace", "default");
        configuration.put("metadata.name", "tma-analyze");
        configuration.put("spec.replicas", "6");
        try {
            RestServices.requestPutRestService(configuration);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
