package Model;

import java.io.Serializable;

public class FlinkResult implements Serializable{

    private String key;
    private double value;
    private long numberOfCars;


    public FlinkResult(String key, double value, long numberOfCars) {
        this.key = key;
        this.value = value;
        this.numberOfCars = numberOfCars;
    }

    public long getNumberOfCars() {
        return numberOfCars;
    }

    public void setNumberOfCars(long numberOfCars) {
        this.numberOfCars = numberOfCars;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}