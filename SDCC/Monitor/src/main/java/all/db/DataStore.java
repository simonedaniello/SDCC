/**
 * 
 */
package all.db;

import main.java.FlinkResult;
import main.java.Semaphore;

import java.util.ArrayList;

/**
 * @author amitesh
 *
 */

public interface DataStore<T> {

	Boolean storeRawEvent(String jsonData);

	T getAll();

	void writeListOfFlinkResultsOnDB(String entryType, ArrayList<FlinkResult> resultList);
}
