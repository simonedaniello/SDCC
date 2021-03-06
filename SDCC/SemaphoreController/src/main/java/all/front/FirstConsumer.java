package all.front;


import all.controllers.CrossroadController;
import all.controllers.Monitorer;
import all.controllers.TwoPCController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.Message;
import entities.system.Printer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;


public class FirstConsumer {

    private TwoPCController twopc;
    private Monitorer monitorer;
    private  String BOOTSTRAP_SERVERS;


    public FirstConsumer(TwoPCController twopc, Monitorer monitorer){

        Properties properties = new Properties();
        String filename = "controllerConfiguration.props";
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
        this.twopc = twopc;
        this.monitorer = monitorer;
    }
    private Consumer<Long, String> consumer;

    private CrossroadController crossroadController;

    public void setController(CrossroadController crossroadController){
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        consumer = new KafkaConsumer<>(props);

    }

    public void runConsumer() {

        Printer.getInstance().print("start listening", "yellow");

        while(true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);
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

            workWithMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void workWithMessage(Message message){
        int code = message.getCode();
        if (code == 1){
            Printer.getInstance().print("arrived semaphore with id: " + message.getSemaphore().getID(), "yellow");
            crossroadController.addSemaphore(message.getSemaphore());
        }
        else if (code == -1){
            crossroadController.removeSemaphore(message.getSemaphore());
        }
        else if (code == 400) {
            monitorer.addSemaphoreToMonitor(message.getSemaphore());
        }
        else if (code == 404) {
            Printer.getInstance().print("\n\n\nARRIVATO MALFUNCTION dal semaforo " + message.getID() + "\n\n\n", "yellow");
            crossroadController.sendMalfunctionToTelegramBot(message);
        }
        //---------------------2PC-----------------------
        else if (code == 311){
            twopc.updateVotes(message.getID());
        }
        else if (code == 312){
/*
            Printer.getInstance().print("OK: " + message.getID(), "blue");
*/
        }
        else if (code == -312){
            twopc.rollback();
        }
        else if (code == 601){
            crossroadController.tellMonitorerToSendInfos(message.getIP(), message.getID());
        }


        //---------------------2PC-----------------------

        else if (code == 200) {
/*
            Printer.getInstance().print("ARRIVATOOOO.---------------------", "red");
*/
            String talpa = message.getSemaphoreTuple();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(talpa);
            if (element.isJsonObject()){
                JsonObject record = element.getAsJsonObject();
                crossroadController.addCarsInQueue(message.getID(),
                        record.get("carsInTimeUnit").getAsDouble(),
                        record.get("meanSpeed").getAsDouble());
            }


        }


    }



}
