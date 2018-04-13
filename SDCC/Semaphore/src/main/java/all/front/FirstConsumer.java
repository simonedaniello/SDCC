package all.front;


import all.newArchitecture.SemaphoreClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.Message;
import main.java.system.Printer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;


public class FirstConsumer {


    private static FirstConsumer instance = new FirstConsumer();

    private FirstConsumer(){
    }

    public static FirstConsumer getInstance() {
        return instance;
    }


    private Consumer<Long, String> consumer;
    private final String BOOTSTRAP_SERVERS =
//            "localhost:9092,localhost:9093,localhost:9094";
            "localhost:9092";


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


    public void runConsumer(String selfTopic) {

        String topicName = "semaphoreKafkaTopic";
        createConsumer();
        subscribeToTopic(topicName);
        subscribeToTopic(selfTopic);
        System.out.println("started listening on kafka on topic:");
        Printer.getInstance().print(topicName, "blue");

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

            Printer.getInstance().print("Consumer Record:( " + record.key() +
                    ", Message: " + message.getID() +
                    ", Code: " + message.getCode() +
                    " )", "cyan");
            workWithMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void workWithMessage(Message message){
        int code = message.getCode();
        if (code == 1){
            SemaphoreClass.getInstance().start2pc(message.isYouAreGreen());
        }
        else if (code == -1){
        }
        else if (code == 10){
            SemaphoreClass.getInstance().updateSemaphoreList(message.getListOfSemaphores());
        }
        else if (code == 401) {
        }
    }
}