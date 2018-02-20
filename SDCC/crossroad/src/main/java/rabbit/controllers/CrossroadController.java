package main.java.rabbit.controllers;

import main.java.rabbit.entities.Crossroad;
import main.java.rabbit.entities.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Author : Simone D'Aniello
 * Date :  20-Feb-18.
 */
public class CrossroadController {

    private Crossroad crossroad;

    public CrossroadController(int ID, String street){
        crossroad = new Crossroad();
        crossroad.setID(ID);
        crossroad.setStreet(street);
    }

    public void addSemaphore(SemaphoreController s) {
        s.addCrossroad(String.valueOf(crossroad.getID()));
        crossroad.getSemaphoreControllers().add(s);
    }

    public void removeSemaphore(Integer x) {
        crossroad.getSemaphoreControllers().removeIf(value -> value.getID().equals(x));
    }

    public void printState(){
        System.out.println("\nCROSSROAD " + crossroad.getID() + " STATE");
        System.out.println("\tStreet: " + crossroad.getStreet());
        System.out.println("\tlist of semaphores:");
        for(SemaphoreController s : crossroad.getSemaphoreControllers()){
            System.out.println("\t\taddress: " + s.getStreet() + ", ID: " + s.getID());
        }
        if(crossroad.getDispatcher() != null){
            System.out.println("\tDispatcher: " + crossroad.getDispatcher());
        } else {
            System.out.println("\tDispatcher not initialized");
        }

    }

    public void receiveState() {

    }

    private void decision() {

    }

    public void sendMessage(int idmessage) {
        Message m = new Message(idmessage);
        Send s1 = new Send(crossroad.getID());
        try {
            s1.sendMessage("localhost", m, "traffic", String.valueOf(crossroad.getID()));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public Integer getID(){
        return crossroad.getID();
    }
}
