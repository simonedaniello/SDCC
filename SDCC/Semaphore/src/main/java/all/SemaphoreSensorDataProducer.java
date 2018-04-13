package all;

import all.model.SemaphoreSensor;
import all.front.FirstProducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SemaphoreSensorDataProducer {

    public static void main(String[] args) {


        while (true) {

            int i = 0;
            List<SemaphoreSensor> eventList = new ArrayList();


            for (i = 0; i < 100; i++) {

                SemaphoreSensor s = new SemaphoreSensor();
                s.initializeSensor();

                eventList.add(s);
            }

            Collections.shuffle(eventList);
            Iterator var15 = eventList.iterator();

            while (var15.hasNext()) {
                SemaphoreSensor event = (SemaphoreSensor) var15.next();
                FirstProducer.getInstance().sendSemaphoreSensorInfo("localhost", event, "semaphoresensor");


            }
        }

    }
}