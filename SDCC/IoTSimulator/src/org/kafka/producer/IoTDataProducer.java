package org.kafka.producer;

import Model.IoTData;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.kafka.utils.PropertyFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoTDataProducer {
    private static final Logger logger = LoggerFactory.getLogger(IoTDataProducer.class);

    private static final String MONGO_HOST = "localhost";
    private static final int MONGO_PORT = 27017;

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
        ObjectMapper mapper = new ObjectMapper();


        while(true) {

                String vehicleId = UUID.randomUUID().toString();
                String timestamp = (new Date()).toString();
                double speed = (double)(rand.nextInt(80) + 20); String coords = this.getCoordinates();
                String latitude = coords.substring(0, coords.indexOf(","));
                String longitude = coords.substring(coords.indexOf(",") + 1, coords.length());
                IoTData event = new IoTData(vehicleId, latitude, longitude, timestamp, speed);
                KeyedMessage<String, IoTData> data = new KeyedMessage(topic, event);
                producer.send(data);
                Thread.sleep((long)(rand.nextInt(200)));
        }
    }

    private String getCoordinates() {

        Random rand = new Random();

        double latPrefix = -37.80;
        double longPrefix = 144.80;

        double lati;
        double longi;

        if (rand.nextInt(100) <=50)
            lati = latPrefix + ((double)rand.nextInt(200)/1000);
        else
            lati = latPrefix - ((double)rand.nextInt(200)/1000);

        if (rand.nextInt(100) <=50)
            longi = longPrefix + ((double)rand.nextInt(200)/1000);
        else
            longi = longPrefix - ((double)rand.nextInt(200)/1000);

        return lati + "," + longi;
    }
}
