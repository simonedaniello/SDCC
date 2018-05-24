package org.Flink;

import main.java.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import producer.FirstProducer;

import java.util.Timer;
import java.util.TimerTask;

public class WindowTrafficData {
    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final String flinkDispatcherID = "IDtoChangeAndRandomize";
    private static final Logger log = LoggerFactory.getLogger(WindowTrafficData.class);
    private static final int TIME_CYCLE = 60000;


    public WindowTrafficData() {
    }

    public static void main(String[] args) {

       try {
  /*          Thread thread1 = new Thread(() -> {
                NewAverageKafkaSender avg = new NewAverageKafkaSender();
                try {
                    avg.calculateAvg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });*/

            Thread thread2 = new Thread(() -> {
                WhichSemaphoreFlink qkafka = new WhichSemaphoreFlink();
                try {
                    qkafka.calculateMedian();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

         //   thread1.start();
            thread2.start();

        //   Timer timer = new Timer();
        //   timer.schedule(new WindowTrafficData.TimerClass(), 0, TIME_CYCLE);

           // thread1.join();
            thread2.join();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static class TimerClass extends TimerTask {
        @Override
        public void run() {
            System.out.println("sending message to monitor");
//            Message m = new Message(flinkDispatcherID, 2001);
//            FirstProducer.getInstance().sendMessage("localhost", m, "monitorer");
        }
    }


}
