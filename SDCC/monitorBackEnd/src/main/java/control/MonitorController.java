package main.java.control;

import main.java.Semaphore;
import main.java.front.Receiver;

import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 */
public class MonitorController {

    private ArrayList<Semaphore> semaphores;
    private String ID;

    public MonitorController(){
        semaphores = new ArrayList<>();
        ID = "aaaaa";
        Receiver r = new Receiver(ID, "traffic", this);
        try {
            r.receiveMessage("localhost", ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Semaphore> getSemaphores() {
        return semaphores;
    }

    public void setSemaphores(ArrayList<Semaphore> semaphores) {
        this.semaphores = semaphores;
        printState();
    }

    private void printState(){
        System.out.println("list of semaphores");
        for(Semaphore s : semaphores)
            System.out.println("\t" + s.getID());
    }
}
