package all.controllers.RNG;

import all.controllers.CrossroadController;
import main.java.Car;
import main.java.Semaphore;
import main.java.system.Printer;

public class ArrivalGenerator {

    private Rvgs rvgs;
    private CrossroadController crossroadController;

    public ArrivalGenerator(CrossroadController crossroadController){

        this.crossroadController = crossroadController;
        //initialize generator and plant seeds
        Rngs r = new Rngs();
        r.plantSeeds(741852096);
        rvgs = new Rvgs(r);

    }



    public void carArrival() {

        //Printer.getInstance().print("chiamo car arrival", "yellow");
        if(crossroadController.getSemaphoreInCrossroad().size() != 0) {
            for (int k = 0; k < 10; k++) {
                int semNumber = (int) Math.floor(rvgs.uniform(0, crossroadController.getSemaphoreInCrossroad().size()));
                String semID = crossroadController.getSemaphoreInCrossroad().get(semNumber).getID();
                double time = rvgs.exponential(1 / 0.4);
                Car car = new Car(time, semID);
                ListOfArrivals.getMe().PushArrival(car);
                for (Semaphore s : crossroadController.getSemaphoreInCrossroad()) {
                    if (car.getSemaphoreID().equals(s.getID()))
                        if(s.getLight() == 0) {
                            s.getQueue().add(car);
                            //Printer.getInstance().print("aggiungo macchina a " + s.getID(), "yellow");
                        }
                }
            }
        }
    }

}