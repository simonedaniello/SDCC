package all.controllers.db;

import main.java.Semaphore;

public class DBcontroller {

    private static DBcontroller instance = new DBcontroller();

    private DBcontroller(){}

    public static DBcontroller getInstance() {
        return instance;
    }

    public void writeIntoDB(String jsonToWrite){

    }

    public void addSemaphoreToCrossroad(String crossroadID, Semaphore semaphore){

    }

    public void removeSemaphoreFromCrossroad(String crossroadID, String semaphoreid){

    }
}
