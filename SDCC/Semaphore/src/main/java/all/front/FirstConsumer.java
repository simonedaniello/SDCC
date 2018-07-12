package all.front;

import all.newArchitecture.SemaphoreClass;
import all.newArchitecture.TwoPCController;
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
import java.util.UUID;


public class FirstConsumer {


    private String crossroad;
    private SemaphoreClass semaphoreClass;
    private TwoPCController twopc;

    private String BOOTSTRAP_SERVERS;

    public FirstConsumer(){

        Properties properties = new Properties();
        String filename = "semaphoresConfig.props";
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


    public void setAttributes(SemaphoreClass s, TwoPCController twopc, String crossroad){
        this.semaphoreClass = s;
        this.twopc = twopc;
        this.crossroad = crossroad;
    }



    private Consumer<Long, String> consumer;


    public void subscribeToTopic(String topic){
        consumer.subscribe(Collections.singletonList(topic));
    }


    private void createConsumer() {


        final Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG,
//                "KafkaExampleConsumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


        consumer = new KafkaConsumer<>(props);

    }


    public void runConsumer(String selfTopic, String crossroadTopic) {

        createConsumer();
        subscribeToTopic(crossroadTopic);
        subscribeToTopic(selfTopic);
/*
        System.out.println("started listening on kafka on topic:");
*/
      /*  Printer.getInstance().print("\t" + crossroadTopic, "blue");
        Printer.getInstance().print("\t" + selfTopic, "blue");*/

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

//            Printer.getInstance().print("Consumer Record:(" + record.key() +
//                    ", Message: " + message.getID() +
//                    ", Code: " + message.getCode() +
//                    ")", "cyan");
            workWithMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message codes:
     *      10:     get the list of semaphores in the crossroad by the controller
     *      301:    the controller has started the voting phase of the 2pc
     *      302:    the controller has started the commit phase of the 2pc
 *          -302:   the controller has started the rollback phase of the 2pc
     * @param message
     */
    private void workWithMessage(Message message){
        int code = message.getCode();
        //-------------monitor and control---------------
        if (code == 10){
            semaphoreClass.updateSemaphoreList(message.getListOfSemaphores());
        }
        //---------------------2PC-----------------------
        else if (code == 301){
            semaphoreClass.start2pc(message.isYouAreGreen(), message.getID());
        }
        else if (code == 302){
            twopc.commitPhase(crossroad);

        }
        else if (code == -302){
            twopc.rollback(crossroad);
        }
        //---------------------Malfunction----------------
        else if(code == 404){
            semaphoreClass.crossroadMalfunction();
        }

    }

    public void setCrossroad(String crossroad) {
        this.crossroad = crossroad;
    }
}
