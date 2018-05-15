package all;

import all.model.SemaphoreSensor;
import all.front.FirstProducer;
import main.java.Semaphore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SemaphoreSensorDataProducer {

    public SemaphoreSensorDataProducer(FirstProducer fp, Semaphore sem) {

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
                if (event.isGreenWorking() == false || event.isYellowWorking() == false || event.isRedWorking() == false)
                        fp.sendSemaphoreSensorInfo("localhost",event, "bulbmalfunctions");
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