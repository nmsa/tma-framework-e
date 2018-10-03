package eubr.atmosphere.tma.actuator.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RestServices {

    private static final String API_ENDPOINT = 
            "http://192.168.122.34:8089/apis/extensions/v1beta1/namespaces/default/deployments/tma-analyze/scale";
    
    public static void requestPutRestService(Map<String, Object> config) throws IOException {
        // FIXME It would be better to use PATCH instead of PUT.
        //       However, the first experiments did not work, and we decided to move with PUT.
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        String namespace = config.get("metadata.namespace").toString();
        String name = config.get("metadata.name").toString();
        String replicas = config.get("spec.replicas").toString();

        String newPayload = "{\n" +
                "  \"metadata\": {\n" +
                "    \"namespace\": \"" + namespace + "\",\n" +
                "    \"name\": \"" + name + "\"\n" +
                "  },\n" +
                "  \"spec\": {\n" +
                "    \"replicas\": " + replicas + "\n" +
                "  }\n" +
                "}";

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
}
