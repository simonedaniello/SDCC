package main.java.front;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
import com.rabbitmq.client.*;
import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.controller.SemaphoreController;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

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

        consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                //doWork(message);
                Message message = SerializationUtils.deserialize(body);
                int code = message.getCode();
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "' with code :'" + code + "' by '" + sc.getSemaphoreID() + "' sent by : "+ message.getID());
                if (code == 1){
                    Semaphore x = message.getSemaphore();
                    ArrayList<Crossroad> list = new ArrayList<>();
                    list.add(new Crossroad(envelope.getRoutingKey(), ""));
                    x.setCrossroads(list);
                    sc.addToSemaphoreList(x);
                }
                else if (code == -1){
                    sc.removeFromSemaphoreList(message.getSemaphore());
                }
                else if (code == 10){
                    sc.setSemaphoreList(message.getListOfSemaphores(), message.getID());
                }
                else if (code == 401) {
                    sc.sendStatus(message.getCurrentCycle());
                }
            }
        };
        for (String binding : bindings){
            this.channel.queueBind(queueName, QUEUE_NAME, binding);
            this.channel.basicConsume(queueName, true, binding ,consumer);
        }

        this.channel.queueBind(queueName, QUEUE_NAME, sc.getSemaphoreID());
        this.channel.basicConsume(queueName, true, sc.getSemaphoreID() ,consumer);

        System.out.println(" [*] Waiting for messages, ID: " + ID);


    }

    public void addBindings(String binding){
        String queueName;
        try {
            queueName = this.channel.queueDeclare().getQueue();
            channel.queueBind(queueName, QUEUE_NAME, binding);
            channel.basicConsume(queueName, true, binding , consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeBinding(String binding){
        try {
            channel.basicCancel(binding);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
