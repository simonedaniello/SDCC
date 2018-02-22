package main.java.controller;

import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.front.Receiver;
import main.java.front.Sender;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

/**
 * Author : Simone D'Aniello
 * Date :  19-Feb-18.
 */

public class SemaphoreController {

    private Semaphore semaphore;
    private Receiver r;
    private Sender s;

    public SemaphoreController(String ID, String address){
        this.semaphore = new Semaphore(ID, address);
        s = new Sender(semaphore.getID());
        r = new Receiver(semaphore.getID(), "traffic", this);
        try {
            r.receiveMessage("localhost", getCrossroadsName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToCrossroad(Crossroad crossroad) {
        Message m = new Message(semaphore.getID(), 1);
        m.setSemaphoreAddress(semaphore.getStreet());
        m.setSemaphoreCode(semaphore.getID());
        try {
            s.sendMessage("localhost", m, "traffic", crossroad.getID());
            System.out.println("adding to crossroad: " + crossroad.getID());
            semaphore.getCrossroads().add(crossroad);
            getReceive().addBindings(crossroad.getID());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void removeCrossroad(Crossroad crossroad) {
        System.out.println("removing crossroad: " + crossroad.getID());
        Message m = new Message(semaphore.getID(), -1);
        m.setSemaphoreAddress(semaphore.getStreet());
        m.setSemaphoreCode(semaphore.getID());
        int k = 0;
        //remove the crossroad and every semaphore which binds that crossroad
        try {
            s.sendMessage("localhost", m, "traffic", crossroad.getID());
            for(Crossroad c: semaphore.getCrossroads()){
                if(c.getID().equals(crossroad.getID())){
                    semaphore.getCrossroads().remove(c);
                    break;
                }
            }
            for (Iterator<Semaphore> iter = semaphore.getSemaphores().listIterator(); iter.hasNext(); ) {
                Semaphore s = iter.next();
                System.out.println("analyzing " + s.getID());
                for(Crossroad c: s.getCrossroads()){
                    if(c.getID().equals(crossroad.getID())){
                        k = 1;
                    }
                }
                if (k == 1) {
                    System.out.println("removing semaphore");
                    iter.remove();
                    k = 0;
                }
            }
            getReceive().removeBinding(crossroad.getID());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private Receiver getReceive() {return this.r; }

    public ArrayList<Semaphore> getListOfSemaphores() {
        return semaphore.getSemaphores();
    }

    private String generateID(){
        return "aaaaa";
    }

    private ArrayList<String> getCrossroadsName(){
        ArrayList<String> toReturn = new ArrayList<>();
        for(Crossroad c : semaphore.getCrossroads()){
            toReturn.add(c.getID());
        }
        return toReturn;
    }

    public void addToSemaphoreList(Semaphore s){
        System.out.println("sono " + semaphore.getID() + " e aggiungo " + s.getID());
        semaphore.getSemaphores().add(s);
    }

    public void removeFromSemaphoreList(Semaphore s) {
        for(Semaphore c: semaphore.getSemaphores()){
            if(c.getID().equals(s.getID())){
                semaphore.getSemaphores().remove(c);
                System.out.println("sono " + semaphore.getID() + " e rimuovo " + c.getID());
                return;
            }
        }
    }

    public void setSemaphoreList(ArrayList<Semaphore> s) {
        for(Semaphore sem : s){
            System.out.println("sono " + semaphore.getID() + " e aggiungo " + sem.getID());
            semaphore.getSemaphores().add(sem);
        }
    }

    public void printState(){
        System.out.println("\nsemaphore id: " + semaphore.getID() + ", street: " + semaphore.getStreet());
        System.out.println("Crossroad list: ");
        for(Crossroad c: semaphore.getCrossroads()){
            System.out.println("\t" + c.getID());
        }
        System.out.println("Semaphore list: ");
        for(Semaphore s : semaphore.getSemaphores()){
            System.out.println("\t" + s.getID());
        }
        System.out.println("Semaphore correlated (greenTogether): ");
        for(Semaphore s : semaphore.getGreenTogether()){
            System.out.println("\t" + s.getID());
        }

    }

    public String getSemaphoreID() {
        return semaphore.getID();
    }

    public void addGreenTogether(Semaphore s){
        semaphore.getGreenTogether().add(s);
    }

    public void removeGreenTogether(Semaphore s) {
        for(Semaphore c: semaphore.getGreenTogether()){
            if(c.getID().equals(s.getID())){
                semaphore.getGreenTogether().remove(c);
                System.out.println("sono " + semaphore.getID() + " e rimuovo da green together " + c.getID());
                return;
            }
        }
    }
}
