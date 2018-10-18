package eubr.atmosphere.tma.actuator;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eubr.atmosphere.tma.actuator.services.RestServices;

@RestController
@RequestMapping("/k8sActuator")
public class KubernetesActuator implements Actuator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesActuator.class);

    @Override
    @PostMapping(path = "/act")
    public void act(@RequestBody ActuatorPayload actuatorPayload) {
        switch (actuatorPayload.getAction()) {
        case "scale":
            scalePods(actuatorPayload);
            break;

        default:
            LOGGER.warn("Not defined action");
            break;
        }
    }

    private void scalePods(ActuatorPayload actuatorPayload) {
        LOGGER.info("action: {}", actuatorPayload.getAction());
        LOGGER.info("resourceId: {}", actuatorPayload.getResourceId());
        LOGGER.info("messageId: {}", actuatorPayload.getMessageId());
        LOGGER.info("timestamp: {}", actuatorPayload.getTimestamp());
        List<Configuration> config = actuatorPayload.getConfiguration();
        LOGGER.info(config.toString());

        try {
            RestServices.requestPutRestService(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
