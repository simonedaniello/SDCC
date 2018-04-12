package all.controllers;


import all.controllers.RNG.ArrivalGenerator;
import all.controllers.RNG.ListOfArrivals;
import main.java.Car;
import main.java.Semaphore;
import main.java.system.Printer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Decider {

    private int [] semaphores = {0,1,2,3};
    private ArrayList<Semaphore> semList = new ArrayList<>();
    private CrossroadController crossroadController;
    private ArrivalGenerator generator;

    private double clock = 0;


    /*
        [0 1 2 3]
        [0 1 2 3]
        [0 1 2 3]
        [0 1 2 3]

        for example, Q(3,2) = 1;    Q(4,4) = 3 --> Q(s,a)
        Nel nostro caso, almeno momentaneamente, avremo 2 colonne, perchè le azioni da intraprendere sono on/off del semaforo
        Il reward lo scrivo solo per farti rosicare. <3 cmunque si fa a cazzo di cane.


     */


    Decider(CrossroadController crossroadController) {
        this.crossroadController = crossroadController;
        generator = new ArrivalGenerator(crossroadController);
        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 1000, 3000); // every 3 seconds
        simulation();

    }

    private void simulation(){



        double upgradeWindow = 15.0;
        int i = 0;
        int timeToSleep;
        int quantoDura = 540;
        while (clock < quantoDura) {
            try {
//                Printer.getInstance().print("chiamo car arrival", "red");

//            System.out.println("Clock: " + clock);
                //   System.out.println("Arrival at semaphore " + car.getSemaphoreID());

//                if (clock >= i * upgradeWindow) {

//                for (i =0;i<4;i++) //test only
//                    greenSemaphore.getQueue().remove(0);
                    learn();
                    Semaphore greenSemaphore = decide();
                    crossroadController.sendGreen(greenSemaphore);
                    timeToSleep = greenSemaphore.getTimes().get(0).intValue();
                    Printer.getInstance().print("il semaforo rimane verde per " + timeToSleep + " secondi", "cyan");
                    TimeUnit.SECONDS.sleep(timeToSleep);
                    i++;
//                    System.out.println("AGGIORNAMENTO");
//                    System.out.println("******************************************************************************************************");
//                }
//                else{
//
//                }
            } catch (IndexOutOfBoundsException e){
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clock ++;
        }

    }

    private Semaphore decide() throws IndexOutOfBoundsException {

        double bestQ = -100;
        String id = crossroadController.getSemaphoreInCrossroad().get(0).getID();
        double temp;

        Semaphore green = null;
        int i = 1;
        for (Semaphore s : crossroadController.getSemaphoreInCrossroad()) {
            temp = s.getTimes().get(1) - s.getTimes().get(2);
            if (temp > bestQ) {
                id = s.getID();
                bestQ = temp;
            }
        }

        for (Semaphore s : crossroadController.getSemaphoreInCrossroad()) {
            if (id.equals(s.getID())) {
                s.setLight(1);
                Printer.getInstance().print("removed " + s.getQueue().size() + " elements from " + s.getID(), "cyan");
                s.getQueue().clear();
                green = s;
                break;
                // System.out.println("Cleared queue for semaphore number " + id);
            } else
                s.setLight(0);
        }

        System.out.println("Turned on semaphore " + id);
        return green;

    }

    private void learn(){


        for (Semaphore s: crossroadController.getSemaphoreInCrossroad()) {
            double alpha = 0.1;
            double gamma = 0.8;
            s.getTimes().set(1,(1- alpha) * (s.getTimes().get(1)) + alpha * (s.getQueue().size() + gamma + s.maxQ()));
            s.getTimes().set(2,(1- alpha) * (s.getTimes().get(2)) + alpha * (gamma + s.maxQ()-s.getQueue().size()));
             //   System.out.println("queue is: " + SemaphoreRepository.getInstance().getList().get(j).getQueue().size());
        }

    }

    private static int reward(Semaphore s){
//        int light = state.getLight();
//        double traffic = state.getTrafficIntensity();
        int queueLenght = s.getQueue().size();
        return  (queueLenght-1)*(20-queueLenght);
    }

    private static int otherReward(Semaphore s){
        int queueLenght = s.getQueue().size();
        return (int) ((1-queueLenght)*Math.exp(queueLenght-10));
    }

    private class TimerClass extends TimerTask {
        @Override
        public void run() {

            generator.carArrival();
            if(ListOfArrivals.getMe().getCarArrivalList().size() != 0) {
                ListOfArrivals.getMe().getCarArrivalList().remove(0);
            }
            //clock += car.getTime();

            //simulation();
        }
    }
}
