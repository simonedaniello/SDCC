/**
 * 
 */
package db;

import com.mongodb.DBCollection;

/**
 * @author amitesh
 *
 */
public interface DataStore<T> {

	Boolean storeRawEvent(String jsonData);

	T getAll();

	Boolean updateController(String id, String json);

    void addFirstCrossroadToMongo(String id, String crossroadName);

    void addSemaphoreToMongo(String crossroadName, String semaphoreName);
}
