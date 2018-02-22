package main.java;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class Semaphore implements Serializable{

    private String ID;
    private Integer state; // 0: free, 1: requested, 2: in CS
    private String street;
    private ArrayList<Crossroad> crossroads;
    private ArrayList<Semaphore> semaphores;
    private ArrayList<Semaphore> greenTogether;

    public Semaphore(String id, String street) {
        this.ID = id;
        this.state = 0;
        this.street = street;
        this.crossroads = new ArrayList<>();
        this.semaphores = new ArrayList<>();
        this.greenTogether = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public ArrayList<Crossroad> getCrossroads() {
        return crossroads;
    }

    public void setCrossroads(ArrayList<Crossroad> crossroads) {
        this.crossroads = crossroads;
    }

    public ArrayList<Semaphore> getSemaphores() {
        return semaphores;
    }

    public void setSemaphores(ArrayList<Semaphore> semaphores) {
        this.semaphores = semaphores;
    }

    public void setGreenTogether(ArrayList<Semaphore> greenTogether) {
        this.greenTogether = greenTogether;
    }

    public ArrayList<Semaphore> getGreenTogether() {
        return greenTogether;
    }
}

