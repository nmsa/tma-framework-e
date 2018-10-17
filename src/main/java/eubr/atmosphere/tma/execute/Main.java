package eubr.atmosphere.tma.execute;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.data.Actuator;
import eubr.atmosphere.tma.data.Configuration;
import eubr.atmosphere.tma.execute.database.ActuatorManager;
import eubr.atmosphere.tma.execute.utils.PropertiesManager;
import eubr.atmosphere.tma.execute.utils.RestServices;

public class Main 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main( String[] args )
    {
        runConsumer();
    }

    private static void runConsumer() {

        Action action = new Action("scale", 100, 5);
        action.addConfiguration(new Configuration("metadata.namespace", "default"));
        action.addConfiguration(new Configuration("metadata.name", "tma-analyze"));
        action.addConfiguration(new Configuration("spec.replicas", "3"));
        try {
            Actuator actuator = ActuatorManager.obtainActuatorByAction(action);
            if (actuator != null) {
                act(actuator, action);
            } else {
                LOGGER.warn("Actuator not found: (ActuatorId = {})", action.getActuatorId());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            sleep(60000);
        }


        Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
        int noMessageFound = 0;
        int maxNoMessageFoundCount = Integer.parseInt(
                PropertiesManager.getInstance().getProperty("maxNoMessageFoundCount"));

        try {
            while (true) {

              ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);

              // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
              if (consumerRecords.count() == 0) {
                  noMessageFound++;

                  if (noMessageFound > maxNoMessageFoundCount) {
                    // If no message found count is reached to threshold exit loop.
                      sleep(2000);
                  } else {
                      continue;
                  }
              }

              // Manipulate the records
              consumerRecords.forEach(record -> {
                  handleAction(record);
               });

              // commits the offset of record to broker.
              consumer.commitAsync();
              sleep(30000);
            }
        } finally {
            consumer.close();
        }
    }

    private static void handleAction(ConsumerRecord<Long, String> record) {
        LOGGER.info(record.toString());
        String stringJsonAction = record.value();
        Action action = new Gson().fromJson(stringJsonAction, Action.class);
        Actuator actuator = ActuatorManager.obtainActuatorByAction(action);
        if (actuator != null) {
            act(actuator, action);
        } else {
            LOGGER.warn("Actuator not found: (ActuatorId = {})", action.getActuatorId());
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void act(Actuator actuator, Action action) {
        // Request the service from the actuator to perform the adaptation
        try {
            RestServices.requestRestService(actuator, action);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
