package eubr.atmosphere.tma.execute.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.data.Actuator;
import eubr.atmosphere.tma.data.Configuration;

public class RestServices {

    private static int messageId = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServices.class);

    public static void requestRestService(Actuator actuator, Action action) throws IOException {
        // Reference: https://www.baeldung.com/java-http-request
        String jsonPayload = getJsonObject(action);
        LOGGER.info(jsonPayload);
        String payload = encryptMessage(actuator, jsonPayload);
        LOGGER.info(payload);

        // TODO: It still needs to add the action
        URL url = new URL(actuator.getAddress() + "/act");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(payload);
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
          LOGGER.info(inputLine);
        }
        in.close();

        con.disconnect();
    }

    private static String getJsonObject(Action action) {
        JsonObject jsonObject = new JsonObject();
        JsonObject configurationJson = new JsonObject();
        for (Configuration config: action.getConfigurationList()) {
            configurationJson.addProperty(config.getKeyName(), config.getValue());
        }

        jsonObject.addProperty("resourceId", action.getResourceId());
        jsonObject.addProperty("messageId", messageId++);
        jsonObject.addProperty("timestamp", Calendar.getInstance().getTimeInMillis());
        jsonObject.addProperty("action", action.getAction());
        jsonObject.add("configuration", configurationJson);
        return jsonObject.toString();
    }

    private static String encryptMessage(Actuator actuator, String message) {
        PublicKey pubKey = KeyManager.getPublicKey(actuator.getPubKey());
        byte[] encryptedMessage = KeyManager.encrypt(message, pubKey);
        LOGGER.info(encryptedMessage.toString());
        return new String(encryptedMessage);
    }
}
