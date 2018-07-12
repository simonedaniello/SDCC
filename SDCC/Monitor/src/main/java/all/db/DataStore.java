/**
 * 
 */
package all.db;


import entities.FlinkResult;

import java.util.List;

/**
 * @author amitesh
 *
 */

public interface DataStore<T> {

	Boolean storeRawEvent(String jsonData);

	T getAll();

	void writeListOfFlinkResultsOnDB(String entryType, List<FlinkResult> resultList);
}
