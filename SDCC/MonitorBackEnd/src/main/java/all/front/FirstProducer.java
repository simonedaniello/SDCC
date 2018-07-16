package all.front;


import entities.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class FirstProducer implements Serializer{


    public static FirstProducer instance = new FirstProducer();
    private Producer<Long, String> producer;

    private String BOOTSTRAP_SERVERS;

    private FirstProducer(){

        Properties properties = new Properties();
        String filename = "monitorBackend.props";
        InputStream input = FirstProducer.class.getClassLoader().getResourceAsStream(filename);
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
        producer = createProducer();

    }

    public static FirstProducer getInstance() {
        return instance;
    }


    private Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }


    public void sendMessage(String address, Message m, String topic) {


        long time = System.currentTimeMillis();


        try {

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


    @Override
    public void close() {

    }
}
