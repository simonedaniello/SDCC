package main.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class Semaphore implements Serializable{

    private String ID;
    private String street;
    private int monitorCycle;
    private ArrayList<Crossroad> crossroads;
    private ArrayList<Semaphore> semaphores;
    private ArrayList<Semaphore> greenTogether;
    private int light; // 0 for red, 1 for green
    private double trafficIntensity;
    private List<Car> queue = new ArrayList<>();
    private  ArrayList<Double> times = new ArrayList<>();


    public Semaphore(){}

    public Semaphore(String id, String street) {
        this.ID = id;
        this.light = 0;
        this.street = street;
        this.crossroads = new ArrayList<>();
        this.semaphores = new ArrayList<>();
        this.greenTogether = new ArrayList<>();
        times.add(3.0);
        times.add(0.0);
        times.add(0.0);
        this.monitorCycle = 0;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public int getMonitorCycle() {
        return monitorCycle;
    }

    public void setMonitorCycle(int monitorCycle) {
        this.monitorCycle = monitorCycle;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public double getTrafficIntensity() {
        return trafficIntensity;
    }

    public void setTrafficIntensity(double trafficIntensity) {
        this.trafficIntensity = trafficIntensity;
    }

    public List<Car> getQueue() {
        return queue;
    }

    public void setQueue(List<Car> queue) {
        this.queue = queue;
    }

    public ArrayList<Double> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<Double> times) {
        this.times = times;
    }

    public double maxQ(){
        return Math.max(times.get(1),times.get(2));
    }
}

