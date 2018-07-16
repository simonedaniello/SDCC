package all.controllers;

import entities.Semaphore;

import java.util.ArrayList;
import java.util.List;

public class ReinforcementLearningController {

    private List<Semaphore> semaphoreList;
    private static double alpha = 0.1;
    private static double gamma = 0.8;
    private CrossroadController crossroadController;


    public ReinforcementLearningController(CrossroadController crossroadController) {

        this.crossroadController = crossroadController;
        semaphoreList = new ArrayList<>();
        Thread thread = new Thread(this::lightUpSemaphore);

        thread.start();
    }

    public void addSemaphore (Semaphore semaphore){
        boolean found = false;

        for (Semaphore s: semaphoreList) {
            if (s.getID().equals(semaphore.getID())) {
                //s.setQueueSize(semaphore.getQueueSize());
                found = true;
            }
        }

        if (!found)
            semaphoreList.add(semaphore);
    }


    public void lightUpSemaphore(){

        while(semaphoreList.size() != 4) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (semaphoreList.size() == 4) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            learn();
            decide();

        }


    }

    private void learn(){


        for (Semaphore s:semaphoreList) {


/*            System.out.println("value 0: " + s.getValues().get(0));
            System.out.println("value 1: " + s.getValues().get(1));
            System.out.println("queue size: " + s.getQueueSize());
            System.out.println("max Q: " + s.maxQ());*/

            //quanto conviene che il semaforo venga acceso
            s.getValues().set(0, (1-alpha) * (s.getValues().get(0)) + alpha* (s.getQueueSize() + gamma + s.maxQ()));

            //quanto convenga che il semaforo venga spento
            s.getValues().set(1, (1-alpha) * (s.getValues().get(1)) + alpha* (gamma + s.maxQ()-s.getQueueSize()));

        }

    }

    private Semaphore decide() {

        double bestQ = -100;
        String id = semaphoreList.get(0).getID();
        double temp;

        Semaphore green = null;
        for (Semaphore s:semaphoreList) {

            /*System.out.println("Semaphore:" + s.getID());
            System.out.println(s.getValues().get(0));
            System.out.println(s.getValues().get(1));
*/
            temp = s.getValues().get(0) - s.getValues().get(1);

            if (temp > bestQ){
                id = s.getID();
                bestQ = temp;
            }

        }

        for (Semaphore s:semaphoreList) {
            if (id.equals(s.getID())) {
                s.setLight(1);
                s.setQueueSize(0);
                green = s;
                //System.out.println("Cleared queue for semaphore number " + id);
            } else
                s.setLight(0);
        }

        //System.out.println("Turned on semaphore " + id);
        crossroadController.setIdToTurnOn(id);
        return green;

    }


    public List<Semaphore> getSemaphoreList() {
        return semaphoreList;
    }

    public void setSemaphoreList(List<Semaphore> semaphoreList) {
        this.semaphoreList = semaphoreList;
    }
}
