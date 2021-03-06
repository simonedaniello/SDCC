package db;

import entities.Semaphore;

public interface DataStore<T> {

	Boolean storeRawEvent(String jsonData);

	T getAll();

	Boolean updateController(String id, String json, String street);

    void addFirstCrossroadToMongo(String id, String crossroadName, String street);

    void addSemaphoreToMongo(String crossroadName, Semaphore semaphore);

    void addMalfunctionToDB(String semaphoreid);
}
