package eubr.atmosphere.tma.actuator.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/securePOC")
public class SecurePOC {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurePOC.class);

    @PostMapping(path = "/act",
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<DataTransferObject> act(@RequestBody String json) {
        LOGGER.info(json);
        return ResponseEntity.ok()
                .header("Custom-Header", "foo")
                .body(new DataTransferObject("TESTE body!"));
    }
}