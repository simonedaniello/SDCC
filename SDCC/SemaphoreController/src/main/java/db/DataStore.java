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
}
