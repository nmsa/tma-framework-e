package eubr.atmosphere.tma.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/ActuatorAPI")
public class ActuatorAPI implements Actuator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorAPI.class);

    @RequestMapping("/initialize")
    public void initialize() {
        // This is for the conceptual model
        LOGGER.info("Initialize :)");
    }

    @RequestMapping("/act")
    @Override
    public void act(@RequestBody ActuatorPayload actuatorPayload) {
        LOGGER.info("Act new!! " + actuatorPayload.toString());
    }
    
    @PostMapping("/actWithResponse")
    public ResponseEntity<String> actFinal(@RequestBody ActuatorPayload actuatorPayload, HttpServletResponse response) {
        LOGGER.info("Act new!! " + actuatorPayload.toString());
        return new ResponseEntity<>(
                    "Example String Response",
                    HttpStatus.OK);
    }

    @RequestMapping("/register")
    protected void register(Actuator callback) {
        // This is for the conceptual model
        LOGGER.info("Register!");
    }
}
