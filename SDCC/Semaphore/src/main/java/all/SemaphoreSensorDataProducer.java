package all;

import all.model.SemaphoreSensor;
import all.front.FirstProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;
import org.springframework.boot.json.GsonJsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SemaphoreSensorDataProducer {

    public SemaphoreSensorDataProducer(FirstProducer fp, Semaphore sem) throws JsonProcessingException {

        while (true) {

            int i = 0;
            List<SemaphoreSensor> eventList = new ArrayList();


            for (i = 0; i < 100; i++) {

                SemaphoreSensor s = new SemaphoreSensor();
                s.initializeSensor(sem);

                eventList.add(s);
            }

            Collections.shuffle(eventList);
            Iterator var15 = eventList.iterator();

            while (var15.hasNext()) {
                SemaphoreSensor event = (SemaphoreSensor) var15.next();
                if (!event.isGreenWorking() || !event.isYellowWorking() || !event.isRedWorking()){
                    ObjectMapper mapper = new ObjectMapper();
                    Printer.getInstance().print("messaggio di malfunction con id: " + sem.getID(), "yellow");
                    Message m = new Message(sem.getID(), 404);
                    m.setSemaphoreTuple(mapper.writeValueAsString(event));
                    fp.sendSemaphoreSensorInfo("localhost", m , sem.getCrossroad());
                }
                fp.sendSemaphoreSensorInfo("localhost", event, "semaphoresensor");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

    }
}