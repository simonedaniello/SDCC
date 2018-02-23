package main.java.controllers;

import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.front.Receiver;
import main.java.front.Sender;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class CrossroadController {

    private Crossroad crossroad;
    private Sender s;

    public CrossroadController(String ID, String address){
        this.crossroad = new Crossroad(ID, address);
        s = new Sender(crossroad.getID());
        Receiver r = new Receiver(crossroad.getID(), "traffic", this);
        Monitorer.getInstance().setSender(s);
        Monitorer.getInstance().setCrossroadID(crossroad.getID());
        try {
            r.receiveMessage("localhost", crossroad.getID());
            r.addBindings("monitor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSemaphore(Semaphore semaphore){
        crossroad.getSemaphores().add(semaphore);
        Message m = new Message(crossroad.getID(), 10);
        m.setListOfSemaphores(crossroad.getSemaphores());
        m.setCurrentCycle(Monitorer.getInstance().getCurrentCycle());
        try {
            s.sendMessage("localhost", m, "traffic", semaphore.getID());
            Monitorer.getInstance().setNumberOfSemaphores(crossroad.getSemaphores().size());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void removeSemaphore(Semaphore semaphore){
        for(Semaphore s : crossroad.getSemaphores()){
            if(s.getID().equals(semaphore.getID())){
                crossroad.getSemaphores().remove(s);
                return;
            }
        }
    }
}
