package RNG;

import Model.Car;
import Model.Semaphore;
import Model.SemaphoreRepository;

public class ArrivalGenerator {

    Rngs r;
    Rvgs rvgs;

    public ArrivalGenerator(){

        //initialize generator and plant seeds
        r = new Rngs();
        r.plantSeeds(741852096);
        rvgs = new Rvgs(r);

    }



    public void carArrival(){

        for (int k = 0; k<10;k++){
        int semID = (int) rvgs.uniform(0,4);
        //System.out.println(semID);
        double time = rvgs.exponential(1/0.4);
        Car car = new Car(time,semID);
        ListOfArrivals.getMe().PushArrival(car);
            for (Semaphore s: SemaphoreRepository.getInstance().getList()) {

                if (car.getSemaphoreID() == s.getId())
                    s.getQueue().add(car);
            }
        }
        return;
    }

}
