/**
 * 
 */
package all.db;

import all.entity.GeneralInfo;

import java.util.ArrayList;

/**
 * @author amitesh
 *
 */

public interface DataStore<T> {

	T getAll();

	public ArrayList<String> getAllCrossroads();

	public ArrayList<String> getAllControllers();

	public String searchController(String controller);

	public String searchCrossroad(String crossroad);

	public GeneralInfo getGeneralInfo();

}
