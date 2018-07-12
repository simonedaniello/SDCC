package all;

import all.model.SemaphoreSensor;
import all.front.FirstProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Message;
import entities.Semaphore;
import entities.system.Printer;
import org.codehaus.jackson.JsonProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SemaphoreSensorDataProducer {

    private String OUTPUT_KAFKA_TOPIC;

    public SemaphoreSensorDataProducer(FirstProducer fp, Semaphore sem) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {


        Properties properties = new Properties();
        String filename = "semaphoresConfig.props";
        InputStream input = Semaphore.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("\n\nSorry, unable to find " + filename);
            return;
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OUTPUT_KAFKA_TOPIC = properties.getProperty("OUTPUT_KAFKA_TOPIC");
        String messageRateMillis= properties.getProperty("messageRateMillis");
        String malfunctionRateInMillis= properties.getProperty("malfunctionRateInTenMill");


        while (true) {
            List<SemaphoreSensor> eventList = new ArrayList();


            for (int i = 0; i < 100; i++) {

                SemaphoreSensor s = new SemaphoreSensor();
                s.initializeSensor(sem, Long.parseLong(malfunctionRateInMillis));

                eventList.add(s);
            }

            Collections.shuffle(eventList);
            Iterator var15 = eventList.iterator();
            ObjectMapper mapper = new ObjectMapper();


            while (var15.hasNext()) {
                SemaphoreSensor event = (SemaphoreSensor) var15.next();
                if (!event.isGreenWorking() || !event.isYellowWorking() || !event.isRedWorking()){
                    Printer.getInstance().print("messaggio di malfunction con id: " + sem.getID(), "yellow");

                    //Messaggio creato quando c'è un malfunzionamento
                    Message m = new Message(sem.getID(), 404);
                    m.setSemaphoreTuple(mapper.writeValueAsString(event));
                    m.setBrokenBulbs(event.getBrokenbulbs());

                    fp.sendSemaphoreSensorInfo("localhost", m , sem.getCrossroad());
                }

                else{
                Message message = new Message(sem.getID(),200);
                message.setSemaphoreTuple(mapper.writeValueAsString(event));


                fp.sendSemaphoreSensorInfo("localhost", message, sem.getCrossroad());

                }

                fp.sendSemaphoreSensorInfo("localhost", event, OUTPUT_KAFKA_TOPIC);

                try {
                    TimeUnit.MILLISECONDS.sleep(Long.parseLong(messageRateMillis));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

    }
}