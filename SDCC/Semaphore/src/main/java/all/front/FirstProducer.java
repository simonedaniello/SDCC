package all.front;


import all.model.SemaphoreSensor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.Message;
import main.java.system.Printer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class FirstProducer implements Serializer{


    final Producer<Long, String> producer;

    public FirstProducer(){
        producer = createProducer();
    }

    private final String BOOTSTRAP_SERVERS =
//            "localhost:9092,localhost:9093,localhost:9094";
            "localhost:9092";


    private Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaSemaphoreProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }


    //GUARDA COME NON INVIARE SOLO IN LOCALHOST
    public void sendMessage(String address, Message m, String topic) {

        long time = System.currentTimeMillis();

        try {

            Printer.getInstance().print("sent message with code: " + m.getCode(), "yellow");
            ObjectMapper mapper = new ObjectMapper();

            String toSend =  mapper.writeValueAsString(m);
            final ProducerRecord<Long, String> record =
                    new ProducerRecord<>(topic, time, toSend);

            producer.send(record).get();

        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
//            producer.flush();
//            producer.close();
        }
    }


    @Override
    public void configure(Map map, boolean b) {

    }


    @Override
    public byte[] serialize(String arg0, Object arg1) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(arg1).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public void sendSemaphoreSensorInfo(String address, SemaphoreSensor s, String topic) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            String toSend =  mapper.writeValueAsString(s);
            System.out.println(toSend);
            final ProducerRecord<Long, String> record =
                    new ProducerRecord<>(topic, toSend);

            producer.send(record).get();

        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
//            producer.flush();
//            producer.close();
        }
    }

    @Override
    public void close() {

    }
}
