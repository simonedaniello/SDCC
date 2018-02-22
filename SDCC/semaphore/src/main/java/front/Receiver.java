package main.java.front;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
import com.rabbitmq.client.*;
import main.java.controller.SemaphoreController;
import main.java.entities.Crossroad;
import main.java.entities.Message;
import main.java.entities.Semaphore;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class Receiver {

    private String ID;
    private String QUEUE_NAME;
    private Channel channel;
    private Consumer consumer;
    private SemaphoreController sc;

    public Receiver(String ID, String queueName, SemaphoreController sc){
        this.ID = ID;
        this.QUEUE_NAME = queueName;
        this.sc = sc;
    }

    public void setQueueName(String queueName){
        this.QUEUE_NAME = queueName;
    }

    public void receiveMessage(String address, ArrayList<String> bindings) throws Exception {

        BasicConfigurator.configure();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setRequestedHeartbeat(2);
        factory.setHost(address);
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        this.channel.exchangeDeclare(QUEUE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = this.channel.queueDeclare().getQueue();

        for (String binding : bindings){
            this.channel.queueBind(queueName, QUEUE_NAME, binding);
        }

        this.channel.queueBind(queueName, QUEUE_NAME, sc.getSemaphoreID());
        System.out.println(" [*] Waiting for messages, ID: " + ID);

        consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                //doWork(message);
                Message message = SerializationUtils.deserialize(body);
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "' with code :'" + message.getCode() + "' sent by : "+ message.getID());
                if (message.getCode() == 1){
                    Semaphore x = new Semaphore(message.getSemaphoreCode(), message.getSemaphoreAddress());
                    ArrayList<Crossroad> list = new ArrayList<>();
                    list.add(new Crossroad(envelope.getRoutingKey(), ""));
                    x.setCrossroads(list);
                    sc.addToSemaphoreList(x);
                }
                else if (message.getCode() == -1){
                    sc.removeFromSemaphoreList(new Semaphore(message.getSemaphoreCode(), message.getSemaphoreAddress()));
                }
                else if (message.getCode() == 10){
                    sc.setSemaphoreList(message.getListOfSemaphores());
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
