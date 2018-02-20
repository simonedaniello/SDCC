package main.java.rabbit.entities;

import main.java.rabbit.controllers.SemaphoreController;

import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  20-Feb-18.
 */
public class Crossroad {

    private ArrayList<SemaphoreController> semaphoreControllers;
    private Integer ID;
    private String street;
    private String dispatcher;

    public  Crossroad(){
        semaphoreControllers = new ArrayList<>();
    }

    public ArrayList<SemaphoreController> getSemaphoreControllers() {
        return semaphoreControllers;
    }

    public void setSemaphoreControllers(ArrayList<SemaphoreController> semaphoreControllers) {
        this.semaphoreControllers = semaphoreControllers;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }
}
