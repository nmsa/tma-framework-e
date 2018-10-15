package eubr.atmosphere.tma.actuator.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eubr.atmosphere.tma.actuator.examples.Configuration;

public class RestServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServices.class);

    private static final String API_ENDPOINT = 
            "http://192.168.122.34:8089/apis/extensions/v1beta1/namespaces/default/deployments/tma-analyze/scale";
    
    public static void requestPutRestService(List<Configuration> config) throws IOException {
        // FIXME It would be better to use PATCH instead of PUT.
        //       However, the first experiments did not work, and we decided to move with PUT.
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        String newPayload = createPayload(config);
        LOGGER.info(newPayload);

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(newPayload);
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
          System.out.println(inputLine);
        }
        in.close();

        con.disconnect();
    }

    private static String createPayload(List<Configuration> config) {
        String namespace = "";
        String name = "";
        String replicas = "";

        for (Configuration conf : config) {
            switch (conf.getKeyName()) {
            case "metadata.namespace":
                namespace = conf.getValue();
                break;

            case "metadata.name":
                name = conf.getValue();
                break;

            case "spec.replicas":
                replicas = conf.getValue();
                break;

            default:
                LOGGER.warn("Unknown value: {}", conf.toString());
                break;
            }
        }

        String newPayload = "{\n" +
                "  \"metadata\": {\n" +
                "    \"namespace\": \"" + namespace + "\",\n" +
                "    \"name\": \"" + name + "\"\n" +
                "  },\n" +
                "  \"spec\": {\n" +
                "    \"replicas\": " + replicas + "\n" +
                "  }\n" +
                "}";
        return newPayload;
    }
}
