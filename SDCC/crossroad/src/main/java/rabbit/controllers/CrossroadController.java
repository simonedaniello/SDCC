package main.java.rabbit.controllers;

import main.java.rabbit.controllers.SemaphoreController;
import main.java.rabbit.entities.Crossroad;

import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  20-Feb-18.
 */
public class CrossroadController {

    Crossroad crossroad;

    public void addSemaphore(SemaphoreController s) {
        crossroad.getSemaphoreControllers().add(s);
    }

    public void removeSemaphore(Integer x) {
        for(SemaphoreController s : crossroad.getSemaphoreControllers()){
            if(s.getID().equals(x)){
                crossroad.getSemaphoreControllers().remove(s);
            }
        }
    }

    public void printState(){
        System.out.println("\nCROSSROAD " + crossroad.getID() + " STATE");
        System.out.println("\tStreet: " + crossroad.getStreet());
        System.out.println("\t list of semaphores:");
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
}
