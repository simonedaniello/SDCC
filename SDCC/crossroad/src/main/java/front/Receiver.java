package main.java.front;

import com.rabbitmq.client.*;
import main.java.controllers.CrossroadController;
import main.java.entities.Message;
import main.java.entities.Semaphore;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */


public class Receiver {

    private String ID;
    private String QUEUE_NAME;
    private Channel channel;
    private Consumer consumer;
    private CrossroadController crossroad;

    public Receiver(String ID, String queueName, CrossroadController crossroad){
        this.ID = ID;
        this.QUEUE_NAME = queueName;
        this.crossroad = crossroad;
    }

    public void setQueueName(String queueName){
        this.QUEUE_NAME = queueName;
    }

    public void receiveMessage(String address, String binding) throws Exception {

        BasicConfigurator.configure();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setRequestedHeartbeat(2);
        factory.setHost(address);
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        this.channel.exchangeDeclare(QUEUE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = this.channel.queueDeclare().getQueue();

        this.channel.queueBind(queueName, QUEUE_NAME, binding);

        System.out.println(" [*] Waiting for messages, ID: " + ID);

        consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                Message message = SerializationUtils.deserialize(body);
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message.getCode() + "'");
                if (message.getCode() == 1){
                    crossroad.addSemaphore(new Semaphore(message.getSemaphoreCode(), message.getSemaphoreAddress()));
                }
                else if (message.getCode() == -1){
                    crossroad.removeSemaphore(new Semaphore(message.getSemaphoreCode(), message.getSemaphoreAddress()));
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
