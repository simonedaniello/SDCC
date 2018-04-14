package all.controllers;

import all.front.FirstConsumer;
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
 * Date :  21-Feb-18.
 */
public class CrossroadController{

    private Crossroad crossroad;
    private TwoPCController twopc;
    private FirstConsumer fc;
    private FirstProducer fp;
    private Monitorer monitorer;

    public CrossroadController(String ID, String address){
        this.crossroad = new Crossroad(ID, address);
        this.fp = new FirstProducer();
        this.twopc = new TwoPCController(fp);
        this.monitorer = new Monitorer(fp);
        this.fc = new FirstConsumer(twopc, monitorer);



        fc.setController(this);
        monitorer.setCrossroad(crossroad);
        try {
            fc.subscribeToTopic("monitor");
            fc.subscribeToTopic(crossroad.getID());

            Thread thread1 = new Thread(() -> {
                System.out.println("starting kafka consumer...");
                fc.runConsumer();
            });

            thread1.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 10000, 15000); // every 15 seconds
        new Decider(this);
    }

    public ArrayList<Semaphore> getSemaphoreInCrossroad(){
        return crossroad.getSemaphores();
    }

    public void addSemaphore(Semaphore semaphore){
        Printer.getInstance().print("adding semapore: " + semaphore.getID(), "green");
        crossroad.getSemaphores().add(semaphore);
        sendCurrentState();
        monitorer.setNumberOfSemaphores(crossroad.getSemaphores().size());
        printSemaphores();
    }

    public void removeSemaphore(Semaphore semaphore){
        for(Semaphore s : crossroad.getSemaphores()){
            if(s.getID().equals(semaphore.getID())){
                System.out.println("removing semaphore " + s.getID());
                crossroad.getSemaphores().remove(s);
                monitorer.setNumberOfSemaphores(crossroad.getSemaphores().size());
                break;
            }
        }
        printSemaphores();
    }

    private void printSemaphores(){
        if(crossroad.getSemaphores().size() != 0) {
            Printer.getInstance().print("semaphores binded: ", "green");
            for (Semaphore s : crossroad.getSemaphores())
                Printer.getInstance().print(s.getID(), "green");
        }
    }

    private void sendCurrentState(){
        Message m = new Message(crossroad.getID(), 10);
        m.setListOfSemaphores(crossroad.getSemaphores());
        m.setCurrentCycle(monitorer.getCurrentCycle());
        for(Semaphore s: crossroad.getSemaphores())
            fp.sendMessage("address", m, s.getID());
    }

    public void sendGreen(Semaphore greenSemaphore) {

    }

    private class TimerClass extends TimerTask {
        @Override
        public void run() {
            sendCurrentState();
            printSemaphores();
            if(crossroad.getSemaphores().size() != 0)
                twopc.votingPhase(crossroad.getSemaphores(), crossroad.getSemaphores().get(0).getID());
        }
    }
}


