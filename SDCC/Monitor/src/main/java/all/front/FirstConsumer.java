package all.front;


import all.controllers.FlinkStatusMonitorer;
import all.controllers.Monitorer;
import com.google.gson.Gson;
import entities.system.Printer;
import entities.Message;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;


public class FirstConsumer {

    private static FirstConsumer instance = new FirstConsumer();
    private Monitorer monitorer;
    private FlinkStatusMonitorer flinkStatusMonitorer;

    private Consumer<Long, String> consumer;
    private String BOOTSTRAP_SERVERS;


    private FirstConsumer(){
        Properties properties = new Properties();
        String filename = "consumer.props";
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

    public void setMonitorer(Monitorer monitorer, FlinkStatusMonitorer flinkStatusMonitorer){
        createConsumer();
        this.monitorer = monitorer;
        this.flinkStatusMonitorer = flinkStatusMonitorer;
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
        if (code == 70115) {
//            Printer.getInstance().print("arrivato un messaggio 701 - 15 minuti", "yellow");
            flinkStatusMonitorer.updateTimeout(message.getID());
            monitorer.addAvgFromKafka15(message.getFlinkResult());
        }
        else if (code == 7011) {
//            Printer.getInstance().print("arrivato un messaggio 701 - 1 ora", "yellow");
            flinkStatusMonitorer.updateTimeout(message.getID());
            monitorer.addAvgFromKafka1(message.getFlinkResult());
        }
        else if (code == 70124) {
//            Printer.getInstance().print("arrivato un messaggio 701 - 24 ore", "yellow");
            flinkStatusMonitorer.updateTimeout(message.getID());
            monitorer.addAvgFromKafka24(message.getFlinkResult());
        }
        //quantile speed
        else if (code == 70215){
//            Printer.getInstance().print("arrivato un messaggio 702 - 15 minuti", "yellow");
            flinkStatusMonitorer.updateTimeout(message.getID());
            monitorer.addQuantilFromKafka15(message.getFlinkResult());
        }
        else if (code == 7021){
//            Printer.getInstance().print("arrivato un messaggio 702 - 1 ora", "yellow");
            flinkStatusMonitorer.updateTimeout(message.getID());
            monitorer.addQuantilFromKafka1(message.getFlinkResult());
        }
        else if (code == 70224){
//            Printer.getInstance().print("arrivato un messaggio 702 - 24 ore", "yellow");
            flinkStatusMonitorer.updateTimeout(message.getID());
            monitorer.addQuantilFromKafka24(message.getFlinkResult());
        }
        //monitoring status message
        else if (code == 2001){
            flinkStatusMonitorer.updateTimeout(message.getID());
        }
    }



}
