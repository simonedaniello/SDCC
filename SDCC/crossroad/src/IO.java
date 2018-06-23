import Model.Car;
import Model.Semaphore;
import Model.SemaphoreRepository;
import RNG.ArrivalGenerator;
import RNG.ListOfArrivals;

import java.util.ArrayList;
import java.util.Random;

public class IO {

    private static int [] semaphores = {0,1,2,3};
    private static double alpha = 0.1;
    private static double gamma = 0.8;
    private static double [][] q = {{0,0},{0,0},{0,0},{0,0}};
    private ArrayList<Semaphore> semList = new ArrayList<>();
    private static int quantoDura = 540;

    private  int f1 = 1;
    private  int f2 = 2;
    private  int f3 = 3;
    private  int f4 = 4;


    /*
        [0 1 2 3]
        [0 1 2 3]
        [0 1 2 3]
        [0 1 2 3]

        for example, Q(3,2) = 1;    Q(4,4) = 3 --> Q(s,a)
        Nel nostro caso, almeno momentaneamente, avremo 2 colonne, perchè le azioni da intraprendere sono on/off del semaforo
        Il reward lo scrivo solo per farti rosicare. <3 cmunque si fa a cazzo di cane.


     */


    public static void main(String[] args) {

        Semaphore s0 = new Semaphore(0,1);
        Semaphore s1 = new Semaphore(1,0);
        Semaphore s2 = new Semaphore(2,0);
        Semaphore s3 = new Semaphore(3,0);

        Semaphore greenSemaphore = s0;

        SemaphoreRepository.getInstance().getList().add(s0);
        SemaphoreRepository.getInstance().getList().add(s1);
        SemaphoreRepository.getInstance().getList().add(s2);
        SemaphoreRepository.getInstance().getList().add(s3);

        ArrivalGenerator generator = new ArrivalGenerator();

        double clock = 0;
        double upgradeWindow = 15.0;
        int i = 0;
        while (clock < quantoDura) {

            generator.carArrival();
            Car car = ListOfArrivals.getMe().getCarArrivalList().remove(0);
            clock += car.getTime();
            System.out.println("Clock: " + clock);
            //   System.out.println("Arrival at semaphore " + car.getSemaphoreID());

            for (Semaphore s: SemaphoreRepository.getInstance().getList()) {

                   System.out.println("queue lenght semaphore " + s.getId() + " is " + s.getQueue().size());


            }

            if (clock >= i*upgradeWindow) {

                for (i =0;i<4;i++) //test only
                    greenSemaphore.getQueue().remove(0);
                learn();
                greenSemaphore =  decide();
                i++;
                System.out.println("AGGIORNAMENTO");
                System.out.println("******************************************************************************************************");
            }

            }


        }


    private static Semaphore decide() {

        double bestQ = -100;
        int id = 2;
        double temp;

        Semaphore green = null;
        for (Semaphore s:SemaphoreRepository.getInstance().getList()) {


            temp = s.getTimes().get(1) - s.getTimes().get(2);

            if (temp > bestQ){
                id = s.getId();
                bestQ = temp;
            }

        }

        for (Semaphore s:SemaphoreRepository.getInstance().getList()) {
            if (id == s.getId()) {
                s.setLight(1);
                s.getQueue().clear();
                green = s;
                // System.out.println("Cleared queue for semaphore number " + id);
            } else
                s.setLight(0);
        }

        System.out.println("Turned on semaphore " + id);
        return green;

    }


    private static void learn(){


        for (Semaphore s:SemaphoreRepository.getInstance().getList()) {

            s.getTimes().set(1,(1-alpha) * (s.getTimes().get(1)) + alpha* (s.getQueue().size() + gamma + s.maxQ()));
            s.getTimes().set(2,(1-alpha) * (s.getTimes().get(2)) + alpha* (gamma + s.maxQ()-s.getQueue().size()));

             //   System.out.println("queue is: " + SemaphoreRepository.getInstance().getList().get(j).getQueue().size());

            }

    }


    private void adjustSemaphoreTime(){



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


    private static double maxQ(int rowIndex){

        return Math.max(q[rowIndex][0],q[rowIndex][1]);

    }

}
