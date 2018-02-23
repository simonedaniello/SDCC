package main.java.controllers;

import main.java.Message;
import main.java.Semaphore;
import main.java.front.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 */
public class Monitorer {

    private static Monitorer monitorer = new Monitorer();
    private ArrayList<Semaphore> semaphoresArrayList = new ArrayList<>();
    private int numberOfSemaphores;
    private int currentCycle;
    private String crossroadID;
    private Sender s;

    private Monitorer() {

        currentCycle = 0;
        numberOfSemaphores = 0;
        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 10000, 10000); // every 10 seconds
    }


    public static Monitorer getInstance(){
        return monitorer;
    }

    int getCurrentCycle() {
        return currentCycle;
    }

    public void addSemaphoreToMonitor(Semaphore s){
        if(s.getMonitorCycle() == currentCycle) {
            semaphoresArrayList.add(s);
            if (semaphoresArrayList.size() == numberOfSemaphores) {
                sendMessage();
                semaphoresArrayList.clear();
                currentCycle ++;
            }
        }
        else if (s.getMonitorCycle() < currentCycle){
            System.out.println("< da implementare, arrivato messaggio con ciclo : " + s.getMonitorCycle());
        }
        else if (s.getMonitorCycle() > currentCycle){
            System.out.println("> da implementare");
        }

        printStatus();
    }

    private void sendRequest(){
        Message m = new Message("400", 401);
        m.setCurrentCycle(currentCycle);
        try {
            s.sendMessage("localhost", m, "traffic",  crossroadID);
        } catch (IOException | TimeoutException e) {
        e.printStackTrace();
        }
    }

    private void sendMessage(){
        Message m = new Message("400", 500);
        m.setListOfSemaphores(semaphoresArrayList);
        try {
            s.sendMessage("localhost", m, "traffic",  "aaaaa");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void printStatus(){
        System.out.println("list of semaphores");
        for(Semaphore s : semaphoresArrayList)
            System.out.println("\t" + s.getID());
    }

    void setNumberOfSemaphores(int numberOfSemaphores) {
        this.numberOfSemaphores = numberOfSemaphores;
    }

    void setSender(Sender s) {
        this.s = s;
    }

    void setCrossroadID(String crossroadID) {
        this.crossroadID = crossroadID;
    }

    private class TimerClass extends TimerTask{

        @Override
        public void run() {
            if (semaphoresArrayList.size() == 0 && numberOfSemaphores != 0)
                sendRequest();
            else
                System.out.println("monitor message not sent");
        }
    }
}
