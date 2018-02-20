package main.java.rabbit.controllers;

import com.rabbitmq.client.*;
import main.java.rabbit.entities.Message;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  13-Feb-18.
 */


public class Receive {

    private Integer ID;
    private String QUEUE_NAME;
    private Channel channel;
    private Consumer consumer;

    public Receive(Integer ID, String queueName){
        this.ID = ID;
        this.QUEUE_NAME = queueName;
    }

    public void setQueueName(String queueName){
        this.QUEUE_NAME = queueName;
    }

    public void receiveMessage(String address, ArrayList<String> bindings) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        //set heartbeat to 2 seconds
        factory.setRequestedHeartbeat(2);
        factory.setHost(address);
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        this.channel.exchangeDeclare(QUEUE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = this.channel.queueDeclare().getQueue();

        for (String binding : bindings){
            this.channel.queueBind(queueName, QUEUE_NAME, binding);
        }

        System.out.println(" [*] Waiting for messages, ID: " + ID);

        consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                try {
                    //doWork(message);
                    Message message = SerializationUtils.deserialize(body);
                    System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message.getID() + "'");
                } finally {
                    System.out.println(" [x] Done");
                    //channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        this.channel.basicConsume(queueName, true, consumer);
    }

    public void addBindings(String binding){
        String queueName;
        try {
            queueName = this.channel.queueDeclare().getQueue();
            this.channel.queueBind(queueName, QUEUE_NAME, binding);
            this.channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeBinding(String binding){
        String queueName;
        try {
            queueName = this.channel.queueDeclare().getQueue();
            this.channel.queueUnbind(queueName, QUEUE_NAME, binding);
            this.channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
