package all.front;


import all.control.MonitorController;
import all.control.QuerySolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Message;
import entities.system.Printer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;


public class FirstConsumer {

    private static FirstConsumer instance = new FirstConsumer();
    private String BOOTSTRAP_SERVERS;


    private FirstConsumer(){
        Properties properties = new Properties();
        String filename = "monitorBackend.props";
        InputStream input = FirstConsumer.class.getClassLoader().getResourceAsStream(filename);
        if (input == null){
            System.out.println("\n\n\n\n\nSorry, unable to find " + filename);
            return;
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BOOTSTRAP_SERVERS = properties.getProperty("BOOTSTRAP_SERVERS");
    }

    public static FirstConsumer getInstance() {
        return instance;
    }
    private Consumer<Long, String> consumer;

    private MonitorController monitorController;
    private QuerySolver qs;


    public void setAttributes(MonitorController monitorController, QuerySolver querySolver){
        createConsumer();
        this.monitorController= monitorController;
        this.qs = querySolver;
    }


    public void subscribeToTopic(String topic){
        consumer.subscribe(Collections.singletonList(topic));
    }


    private void createConsumer() {


        final Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        consumer = new KafkaConsumer<>(props);

    }


    public void runConsumer() {

        //createConsumer();
        //subscribeToTopic("my-example-topic");

        final int giveUp = 100;   int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

//            if (consumerRecords.count()==0) {
//                noRecordsCount++;
//                if (noRecordsCount > giveUp) break;
//                else continue;
//            }
            consumerRecords.forEach(this::DeserializeMessage);

            consumer.commitAsync();
        }
//        consumer.close();
//        System.out.println("DONE");
    }


    private void DeserializeMessage(ConsumerRecord<Long, String> record){
        ObjectMapper mapper = new ObjectMapper();

        //JSON from String to Object
        try {
            Message message = mapper.readValue(record.value(), Message.class);

         /*   Printer.getInstance().print("Consumer Record:( " + record.key() +
                    ", Message: " + message.getID() +
                    ", Code: " + message.getCode() +
                    " )", "cyan");*/
            workWithMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void workWithMessage(Message message){
        int code = message.getCode();

        if (code == 500) {
            monitorController.addCrossroadToList(message.getCrossroad());
        }
        else if(code == 621){
            //add crossroad response from flinkDispatcher
/*
            Printer.getInstance().print("arrivata response dal crossroad", "yellow");
*/
            qs.sems = message.getListOfSemaphores();
            qs.controllerResponse = "OK";
        }
        else if(code == 622){
            //add semaphore response from flinkDispatcher
            qs.controllerResponse = "OK";
        }
        else if(code == 623){
            ObjectMapper mapper = new ObjectMapper();
            try {
                qs.controllerResponse = mapper.writeValueAsString(message.getListOfSemaphores());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
