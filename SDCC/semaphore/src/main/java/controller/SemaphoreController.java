package main.java.controller;

import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.front.Receiver;
import main.java.front.Sender;
import main.java.system.Printer;


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
        semaphore.getCrossroads().add(crossroad);
        Message m = new Message(semaphore.getID(), 1);
        m.setSemaphore(semaphore);
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
        m.setSemaphore(semaphore);
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
            Printer.getInstance().print("analyzing " + c.getID(), "red");
            if(c.getID().equals(s.getID())){
                semaphore.getSemaphores().remove(c);
                Printer.getInstance().print("sono " + semaphore.getID() + " e rimuovo " + c.getID(), "red");
                return;
            }
        }
    }

    public void setSemaphoreList(ArrayList<Semaphore> newSem , String crossroad) {
        int k = 0;
        Printer.getInstance().print("removing semaphores from crossroad " + crossroad, "cyan");
        for (Iterator<Semaphore> iter = semaphore.getSemaphores().listIterator(); iter.hasNext(); ) {
            Semaphore s = iter.next();
            System.out.println("analyzing " + s.getID());
            if(s.getCrossroads().size() == 0)
                Printer.getInstance().print("size = 0", "red");
            for(Crossroad c: s.getCrossroads()){
                if(c.getID().equals(crossroad)){
                    System.out.println("ha quell'incrocio");
                    k = 1;
                } else
                    System.out.println("non aveva quell'incrocio");
            }
            if (k == 1) {
                Printer.getInstance().print("removing semaphore " + s.getID(), "red");
                iter.remove();
                k = 0;
            }
        }
        for(Semaphore sem : newSem){
            System.out.println("sono " + semaphore.getID() + " e aggiungo " + sem.getID());
            semaphore.getSemaphores().add(sem);
        }
    }

    public void printState(){
        Printer.getInstance().print("\n semaphore id: " + semaphore.getID(), "blue");
        System.out.println("street: " + semaphore.getStreet());
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

    public void sendStatus(int currentCycle) {
        Message m = new Message(semaphore.getID(), 400);
        semaphore.setMonitorCycle(currentCycle);
        m.setSemaphore(semaphore);
        try {
            s.sendMessage("localhost", m, "traffic", "monitor");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
