package eubr.atmosphere.tma.execute.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.data.Actuator;
import eubr.atmosphere.tma.data.Configuration;

public class RestServices {

    public static void requestRestService(Actuator actuator, Action action) throws IOException {
        // Reference: https://www.baeldung.com/java-http-request
        
        URL url = new URL(actuator.getAddress());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        
        Map<String, String> parameters = new HashMap<>();
        for (Configuration conf : action.getConfigurationList()) {
            parameters.put(conf.getKeyName(), conf.getValue());
        }
         
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
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
