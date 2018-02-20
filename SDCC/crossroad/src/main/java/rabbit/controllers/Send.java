package main.java.rabbit.controllers;

/**
 * Author : Simone D'Aniello
 * Date :  13-Feb-18.
 */



import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import main.java.rabbit.entities.Message;
import org.apache.commons.lang3.SerializationUtils;


public class Send {

    private Integer ID;

    public Send(Integer ID){
        this.ID = ID;
    }

    public void sendMessage(String address, Message message, String queue, String topic) throws java.io.IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(address);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(queue, BuiltinExchangeType.DIRECT);

        byte[] data = SerializationUtils.serialize(message);
        channel.basicPublish(queue, topic, null, data);
        System.out.println(" [x] Sent '" + topic + "' by sender: " + ID);

        channel.close();
        connection.close();
    }

}