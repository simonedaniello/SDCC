package all.front;


import all.controllers.Monitorer;
import com.google.gson.Gson;
import main.java.Message;
import main.java.system.Printer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.util.Collections;
import java.util.Properties;


public class FirstConsumer {

    private static FirstConsumer instance = new FirstConsumer();
    private Monitorer monitorer;


    private FirstConsumer(){
    }

    public static FirstConsumer getInstance() {
        return instance;
    }

    private Consumer<Long, String> consumer;
    private final String BOOTSTRAP_SERVERS =
//            "localhost:9092,localhost:9093,localhost:9094";
            "localhost:9092";





    public void setMonitorer(Monitorer monitorer){
        createConsumer();
        this.monitorer = monitorer;
    }


    public void subscribeToTopic(String topic){
//        createConsumer();
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
        Printer.getInstance().print("start listening", "yellow");

        final int giveUp = 100;   int noRecordsCount = 0;

        while(true) {
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
//        ObjectMapper mapper = new ObjectMapper();

        //JSON from String to Object
        Gson gson = new Gson();
        System.out.println(record.value());
        Message message = gson.fromJson(record.value(), Message.class);

        Printer.getInstance().print("Consumer Record:( " + record.key() +
                ", Message: " + message.getID() +
                ", Code: " + message.getCode() +
                " )", "cyan");
        workWithMessage(message);

    }


    private void workWithMessage(Message message){
        int code = message.getCode();
        //average speed
        if (code == 701) {
            Printer.getInstance().print("arrivato un messaggio 701", "yellow");
            monitorer.addAvgFromKafka(message.getFlinkResult());
        }
        //quantile speed
        else if (code == 702){
            Printer.getInstance().print("arrivato un messaggio 702", "yellow");
            monitorer.addQuantilFromKafka(message.getFlinkResult());
        }
    }



}
