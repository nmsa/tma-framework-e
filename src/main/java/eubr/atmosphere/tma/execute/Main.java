package eubr.atmosphere.tma.execute;

import java.io.IOException;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.data.Actuator;
import eubr.atmosphere.tma.execute.utils.PropertiesManager;
import eubr.atmosphere.tma.execute.utils.RestServices;

/**
 * Hello world!
 *
 */
public class Main 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main( String[] args )
    {
        runConsumer();
    }

    private static void runConsumer() {

        /*Action action = new Action("", 10);
        act(obtainActuator(action), action);*/

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
                  validateValue(record);
               });

              // commits the offset of record to broker.
              consumer.commitAsync();
              sleep(30000);
            }
        } finally {
            consumer.close();
        }
    }

    private static void validateValue(ConsumerRecord<Long, String> record) {
        LOGGER.info(record.toString());
        String stringJsonAction = record.value();
        Action action = new Gson().fromJson(stringJsonAction, Action.class);
        act(obtainActuator(action), action);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Actuator obtainActuator(Action action) {
        // This method can go to TMA-K component

        // TODO: It needs to select the data from the database
        Actuator actuator = new Actuator();
        actuator.setAddress("http://192.168.122.34:8089/apis/extensions/v1beta1/namespaces/default/deployments/tma-analyze/scale");
        actuator.setPubKey("my-key");

        return actuator;
    }

    private static void act(Actuator actuator, Action action) {
        // Perform the adaptation (Part of it will be moved to the Actuator)
        try {
            RestServices.requestPutRestService(actuator, action);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // TODO: Request the REST service (actuator), with the definition from the Action
        /*LOGGER.info("ACTUATION TO BE IMPLEMENTED: " + actuator);
        actuator.setAddress("https://jsonplaceholder.typicode.com/posts");
        try {
            RestServices.requestRestService(actuator, action);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
}
