package main.java.controllers;

import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.front.Sender;
import main.java.system.Printer;

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
    private Crossroad crossroad;

    private Monitorer() {

        currentCycle = 0;
        numberOfSemaphores = 0;
        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 10000, 15000); // every 15 seconds
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
            System.out.println("semaphore.arraylist.size = " + semaphoresArrayList.size());
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
        System.out.println("sending message to monitor");
        printStatus();
        Message m = new Message("400", 500);
        m.setListOfSemaphores(semaphoresArrayList);
        try {
            s.sendMessage("localhost", m, "traffic",  "monitor");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest2(){
        System.out.println("sending message to monitor");
        printStatus();
        Message m = new Message("400", 500);
        m.setCrossroad(crossroad);
        try {
            s.sendMessage("localhost", m, "traffic",  "monitor");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void printStatus(){
        System.out.println("list of semaphores");
        for(Semaphore s : semaphoresArrayList)
            Printer.getInstance().print("\t" + s.getID(), "green");
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

    public void setCrossroad(Crossroad crossroad) {
        this.crossroad = crossroad;
    }

    private class TimerClass extends TimerTask{

        @Override
        public void run() {
            sendRequest2();
        }
    }
}
