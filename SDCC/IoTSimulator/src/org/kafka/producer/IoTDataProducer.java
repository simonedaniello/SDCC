package org.kafka.producer;

import Model.IoTData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.kafka.utils.PropertyFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoTDataProducer {
    private static final Logger logger = LoggerFactory.getLogger(IoTDataProducer.class);

    public IoTDataProducer() {
    }

    public static void main(String[] args) throws Exception {
        Properties prop = PropertyFileReader.readPropertyFile();
        String zookeeper = prop.getProperty("com.iot.app.kafka.zookeeper");
        String brokerList = prop.getProperty("com.iot.app.kafka.brokerlist");
        String topic = prop.getProperty("com.iot.app.kafka.topic");
        logger.info("Using Zookeeper=" + zookeeper + " ,Broker-list=" + brokerList + " and topic " + topic);
        Properties properties = new Properties();
        properties.put("zookeeper.connect", zookeeper);
        properties.put("metadata.broker.list", brokerList);
        properties.put("request.required.acks", "1");
        properties.put("serializer.class", "org.kafka.utils.IoTDataEncoder");
        Producer<String, IoTData> producer = new Producer(new ProducerConfig(properties));
        IoTDataProducer iotProducer = new IoTDataProducer();
        iotProducer.generateIoTEvent(producer, topic);
    }

    private void generateIoTEvent(Producer<String, IoTData> producer, String topic) throws InterruptedException {
        Random rand = new Random();
        logger.info("Sending events");

        while(true) {
            List<IoTData> eventList = new ArrayList();

            for(int i = 0; i < 100; ++i) {
                String vehicleId = UUID.randomUUID().toString();
                String timestamp = (new Date()).toString();
                double speed = (double)(rand.nextInt(80) + 20);

                for(int j = 0; j < 5; ++j) {
                    String coords = this.getCoordinates();
                    String latitude = coords.substring(0, coords.indexOf(","));
                    String longitude = coords.substring(coords.indexOf(",") + 1, coords.length());
                    IoTData event = new IoTData(vehicleId, latitude, longitude, timestamp, speed);
                    eventList.add(event);
                }
            }

            Collections.shuffle(eventList);
            Iterator var15 = eventList.iterator();

            while(var15.hasNext()) {
                IoTData event = (IoTData)var15.next();
                KeyedMessage<String, IoTData> data = new KeyedMessage(topic, event);
                producer.send(data);
                Thread.sleep((long)(rand.nextInt(2000) + 1000));
            }
        }
    }

    private String getCoordinates() {
        Random rand = new Random();

        int latPrefix = 33;
        int longPrefix = -96;
        Float lati = (float)latPrefix + rand.nextFloat();
        Float longi = (float)longPrefix + rand.nextFloat();
        return lati + "," + longi;
    }
}
