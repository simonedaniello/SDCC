package all;

import all.model.SemaphoreSensor;
import all.front.FirstProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;
import org.springframework.boot.json.GsonJsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SemaphoreSensorDataProducer {

    public SemaphoreSensorDataProducer(FirstProducer fp, Semaphore sem) throws JsonProcessingException {

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


        String messageRateMillis= properties.getProperty("messageRateMillis");
        String malfunctionRateInMillis= properties.getProperty("malfunctionRateInTenMill");


        while (true) {

            int i = 0;
            List<SemaphoreSensor> eventList = new ArrayList();


            for (i = 0; i < 100; i++) {

                SemaphoreSensor s = new SemaphoreSensor();
                s.initializeSensor(sem, Long.parseLong(malfunctionRateInMillis));

                eventList.add(s);
            }

            Collections.shuffle(eventList);
            Iterator var15 = eventList.iterator();

            while (var15.hasNext()) {
                SemaphoreSensor event = (SemaphoreSensor) var15.next();
                if (!event.isGreenWorking() || !event.isYellowWorking() || !event.isRedWorking()){
                    ObjectMapper mapper = new ObjectMapper();
                    Printer.getInstance().print("messaggio di malfunction con id: " + sem.getID(), "yellow");

                    //Messaggio creato quando c'è un malfunzionamento
                    Message m = new Message(sem.getID(), 404);
                    m.setSemaphoreTuple(mapper.writeValueAsString(event));
                    m.setBrokenBulbs(event.getBrokenbulbs());

                    fp.sendSemaphoreSensorInfo("localhost", m , sem.getCrossroad());
                }
                fp.sendSemaphoreSensorInfo("localhost", event, "semaphoresensor");
                try {
                    TimeUnit.MILLISECONDS.sleep(Long.parseLong(messageRateMillis));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

    }
}