package main.java;

public class FlinkResult {

    String key;
    double value;

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

    public FlinkResult(String key, double value){
        this.key = key;
        this.value = value;

    }


}
