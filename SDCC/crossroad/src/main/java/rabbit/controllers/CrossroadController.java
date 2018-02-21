package main.java.rabbit.controllers;

import main.java.rabbit.entities.Crossroad;
import main.java.rabbit.entities.Message;
import main.java.rabbit.entities.Semaphore;
import main.java.rabbit.front.Receiver;
import main.java.rabbit.front.Sender;

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
        this.s = new Sender(crossroad.getID());
        r = new Receiver(crossroad.getID(), "traffic", this);
        try {
            r.receiveMessage("localhost", crossroad.getID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSemaphore(Semaphore semaphore){
        crossroad.getSemaphores().add(semaphore);
        Message m = new Message("0", 10);
        m.setListOfSemaphores(crossroad.getSemaphores());
        try {
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
