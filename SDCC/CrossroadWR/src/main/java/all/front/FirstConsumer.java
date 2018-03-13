package all.front;


import all.controllers.CrossroadController;
import all.controllers.Monitorer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import main.java.Message;
import main.java.system.Printer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.io.IOException;
import java.util.Collections;
import java.util.Properties;


public class FirstConsumer {

    private Consumer<Long, String> consumer;
    private final String BOOTSTRAP_SERVERS =
//            "localhost:9092,localhost:9093,localhost:9094";
            "localhost:9092";

    private CrossroadController crossroadController;


    public FirstConsumer(CrossroadController crossroadController){
        createConsumer();
        this.crossroadController = crossroadController;
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

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }
            consumerRecords.forEach(this::DeserializeMessage);

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
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
        if (message.getCode() == 1){
            crossroadController.addSemaphore(message.getSemaphore());
            Printer.getInstance().print("arrived semaphore", "yellow");
        }
        else if (message.getCode() == -1){
            crossroadController.removeSemaphore(message.getSemaphore());
        }
        else if (message.getCode() == 400) {
            Monitorer.getInstance().addSemaphoreToMonitor(message.getSemaphore());
        }
    }

}
