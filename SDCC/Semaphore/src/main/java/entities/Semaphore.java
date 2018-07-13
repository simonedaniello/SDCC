package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class Semaphore implements Serializable{

    private String ID;
    private String street;
    private int monitorCycle;
    private int kafkaport;
    private ArrayList<Semaphore> semaphores;
    private String greenTogether;
    private String crossroad;
    private String controllerIP;
    private int light; // 0 for red, 1 for green, 2 for yellow
    private double trafficIntensity;
    private ArrayList<Double> times = new ArrayList<>();
    private Double queueSize;
    private ArrayList<Double> values;
    private String latitude;
    private String longitude;
    private int order;
    private int malfunctions = 0;


    public Semaphore(){
        values = new ArrayList<>();
        values.add(0.0);
        values.add(0.0);
        queueSize = 0.0;
        this.light = 0;
        this.street = street;
        this.semaphores = new ArrayList<>();
        times.add(8.0);
        times.add(0.0);
        times.add(0.0);
        this.monitorCycle = 0;
    }

    public Semaphore(String id, String street) {
        values = new ArrayList<>();
        values.add(0.0);
        values.add(0.0);
        queueSize = 0.0;
        this.ID = id;
        this.light = 0;
        this.street = street;
        this.semaphores = new ArrayList<>();
        times.add(8.0);
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

    public ArrayList<Semaphore> getSemaphores() {
        return semaphores;
    }

    public void setSemaphores(ArrayList<Semaphore> semaphores) {
        this.semaphores = semaphores;
    }

    public void setGreenTogether(String greenTogether) {
        this.greenTogether = greenTogether;
    }

    public String getGreenTogether() {
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

    public ArrayList<Double> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<Double> times) {
        this.times = times;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getCrossroad() {
        return crossroad;
    }

    public void setCrossroad(String crossroad) {
        this.crossroad = crossroad;
    }

    public String getControllerIP() {
        return controllerIP;
    }

    public void setQueueSize(Double queueSize) {
        this.queueSize = queueSize;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public void setControllerIP(String controllerIP) {
        this.controllerIP = controllerIP;
    }

    public int getKafkaport() {
        return kafkaport;
    }

    public void setKafkaport(int kafkaport) {
        this.kafkaport = kafkaport;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getMalfunctions() {
        return malfunctions;
    }

    public void setMalfunctions(int malfunctions) {
        this.malfunctions = malfunctions;
    }

    public Double getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(double queueSize) {
        this.queueSize = queueSize;
    }

    public double maxQ() {

        return values.get(0) - values.get(1);
    }
}

