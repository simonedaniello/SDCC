package Model;

public class Car {

    private double time;
    private int semaphoreID;
    private double speed; //TODO in future, it'll be nice to considerate different speed for different cars.
                          //todo Apply when simulation policies are defined


    public Car(double t, int id){

        time = t;
        semaphoreID = id;
    }

    public int getSemaphoreID() {
        return semaphoreID;
    }

    public void setSemaphoreID(int semaphoreID) {
        this.semaphoreID = semaphoreID;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
