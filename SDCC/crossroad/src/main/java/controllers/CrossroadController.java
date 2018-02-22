package main.java.controllers;

import main.java.entities.Crossroad;
import main.java.entities.Message;
import main.java.entities.Semaphore;
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
    private Receiver r;
    private Sender s;

    public CrossroadController(String ID, String address){
        this.crossroad = new Crossroad(ID, address);
        s = new Sender(crossroad.getID());
        r = new Receiver(crossroad.getID(), "traffic", this);
        try {
            r.receiveMessage("localhost", crossroad.getID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSemaphore(Semaphore semaphore){
        crossroad.getSemaphores().add(semaphore);
        Message m = new Message(crossroad.getID(), 10);
        m.setListOfSemaphores(crossroad.getSemaphores());
        try {
            System.out.println("invio la lista a " + semaphore.getID());
            s.sendMessage("localhost", m, "traffic", semaphore.getID());
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
