package all.front;

import all.control.MonitorController;
import com.rabbitmq.client.*;
import main.java.Message;
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
    private MonitorController monitorController;

    public Receiver(String ID, String queueName, MonitorController monitorController){
        this.ID = ID;
        this.QUEUE_NAME = queueName;
        this.monitorController = monitorController;
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
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(QUEUE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, QUEUE_NAME, binding);

        System.out.println(" [*] Waiting for messages, ID: " + ID);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                Message message = SerializationUtils.deserialize(body);
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message.getCode() + "'");
                if (message.getCode() == 500) {
                    monitorController.addCrossroadToList(message.getCrossroad());
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

}
