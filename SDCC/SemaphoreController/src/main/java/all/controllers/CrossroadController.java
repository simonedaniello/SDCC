package all.controllers;

import all.front.FirstConsumer;
import all.front.FirstProducer;
import db.MongoDataStore;
import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.net.UnknownHostException;
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
        timer.schedule(new TimerClass(), 5000, 5000); // every 5 seconds
    }

    public ArrayList<Semaphore> getSemaphoreInCrossroad(){
        return crossroad.getSemaphores();
    }

    public void addSemaphore(Semaphore semaphore){
        Printer.getInstance().print("adding semapore: " + semaphore.getID(), "green");
        giveOrderingToSemaphore(semaphore);
        crossroad.getSemaphores().add(semaphore);
        sendCurrentState();
        monitorer.setNumberOfSemaphores(crossroad.getSemaphores().size());
        try {
            MongoDataStore.getInstance().addSemaphoreToMongo(crossroad.getID(), semaphore.getID());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
        for(Semaphore s: crossroad.getSemaphores()) {
            fp.sendMessage("address", m, s.getID());
            Printer.getInstance().print("\n\ncurrent state sent to " + s.getID() +"\n\n", "yellow");
        }
    }

    public void sendGreen(Semaphore greenSemaphore) {

    }

    private class TimerClass extends TimerTask {
        private int times = 0;
        @Override
        public void run() {
            sendCurrentState();
            printSemaphores();
            if(crossroad.getSemaphores().size() != 0) {
                if (times == 2)
                    twopc.votingPhase(crossroad.getSemaphores(), crossroad.getSemaphores().get(0).getID());
                else
                    times++;
            }
        }
    }

    private void giveOrderingToSemaphore(Semaphore s){
        if(crossroad.getSemaphores().size() == 0)
            s.setOrder(0);
        else
            s.setOrder(crossroad.getSemaphores().get(crossroad.getSemaphores().size()-1).getOrder() + 1);
        System.out.println("imposto ordinamento a " + s.getOrder());
    }
}


