package all.controllers;

import all.front.FirstProducer;
import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 */

public class Monitorer {

    private ArrayList<Semaphore> semaphoresArrayList = new ArrayList<>();
    private int numberOfSemaphores;
    private int currentCycle;
    private Crossroad crossroad;
    private FirstProducer fp;

    public  Monitorer(FirstProducer fp) {
        this.fp = fp;
        currentCycle = 0;
        numberOfSemaphores = 0;
        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 10000, 15000); // every 15 seconds
    }

    int getCurrentCycle() {
        return currentCycle;
    }

    public void addSemaphoreToMonitor(Semaphore s){
            semaphoresArrayList.add(s);
    }

    private void sendMessage(){
        System.out.println("sending message to monitor");
        printStatus();
        Message m = new Message("400", 500);
        m.setListOfSemaphores(semaphoresArrayList);
        fp.sendMessage("localhost", m, "monitor");
    }

    private void sendRequest2(){
        System.out.println("sending message to monitor");
        printStatus();
        Message m = new Message("400", 500);
        m.setCrossroad(crossroad);
        fp.sendMessage("localhost", m,"monitor");
    }

    private void printStatus(){
        System.out.println("list of semaphores");
        for(Semaphore s : semaphoresArrayList)
            Printer.getInstance().print("\t" + s.getID(), "green");
    }

    void setNumberOfSemaphores(int numberOfSemaphores) {
        this.numberOfSemaphores = numberOfSemaphores;
    }

    public void setCrossroad(Crossroad crossroad) {
        this.crossroad = crossroad;
    }

    public void sendCrossroadSituation(String ip, String id) {
        Printer.getInstance().print("invio le informazioni", "green");
        Message m = new Message("400", 621);
        m.setListOfSemaphores(crossroad.getSemaphores());
        fp.sendMessage("localhost", m, id);
    }

    private class TimerClass extends TimerTask{

        @Override
        public void run() {
            sendRequest2();
        }
    }
}
