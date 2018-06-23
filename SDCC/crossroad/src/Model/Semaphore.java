package Model;

import java.util.ArrayList;
import java.util.List;

public class Semaphore {

    private int id;
    private int light; // 0 for red, 1 for green
    private double trafficIntensity;
    private List<Car> queue = new ArrayList();
    private  ArrayList<Double> times = new ArrayList<>();


    //varie ed eventuali

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Semaphore(int id, int light){

        this.id = id;

        this.light = light;

        times.add(3.0);
        times.add(0.0);
        times.add(0.0);
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

    public double maxQ(){

        return Math.max(times.get(1),times.get(2));

    }

}
