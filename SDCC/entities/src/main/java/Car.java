package main.java;

import java.io.Serializable;

public class Car implements Serializable{

    private double time;
    private String semaphoreID;
    private double speed; //TODO in future, it'll be nice to considerate different speed for different cars.
                          //todo Apply when simulation policies are defined


    public Car(){}

    public Car(double t, String id){

        time = t;
        semaphoreID = id;
    }

    public String getSemaphoreID() {
        return semaphoreID;
    }

    public void setSemaphoreID(String semaphoreID) {
        this.semaphoreID = semaphoreID;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
