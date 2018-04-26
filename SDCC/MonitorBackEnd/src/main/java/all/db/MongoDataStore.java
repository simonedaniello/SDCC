/**
 * 
 */
package all.db;

import all.entity.GeneralInfo;
import com.mongodb.*;
import com.mongodb.util.JSON;
import main.java.system.Printer;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MongoDataStore implements DataStore {

	private static DataStore mongoDataStore;
	private static DBCollection rawEventsColl;
	public static final String COLLECTION_NAME = "events";
	private String controllerid = null;

	@Autowired
	private MongoDataStore() {
	}

	/**
	 * @param mongoHost
	 * @param mongoPort
	 * @return
	 * @throws UnknownHostException
	 */

	
	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	public static DataStore getInstance()
			throws UnknownHostException {
		synchronized (MongoDataStore.class) {
			if (mongoDataStore == null) {
				DB db = new MongoClient(MONGO_HOST,MONGO_PORT).getDB("stream");
				rawEventsColl = db.getCollection("events");
				mongoDataStore = new MongoDataStore();
			}
		}
		return mongoDataStore;
	}



	@Override
	public List<ServiceEventRequest> getAll() {

		BasicDBObject query = new BasicDBObject();
		DBCursor cursor = rawEventsColl.find(query);
		DBObject data = null;
		List<ServiceEventRequest> list = new ArrayList<>();
		while (cursor.hasNext()) {
			data = cursor.next();

			System.out.println("PRINT data::" + data);
			ServiceEventRequest request = new ServiceEventRequest();
			request.setName((String) data.get("name"));
			list.add(request);
		}
		System.out.println("PRINT data after while::" + list);
		return list;
	}

    public void printAllDocuments(DBCollection collection) {
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            Printer.getInstance().print(cursor.next().toString(), "green");
        }
    }

	public String searchCrossroad(String crossroad){
		BasicDBObject query = new BasicDBObject("_id", crossroad);
		DBObject obj = rawEventsColl.findOne(query);
		return obj.toString();
	}

	public String searchController(String controller){
        BasicDBObject query = new BasicDBObject("_id", controller);
        DBObject obj = rawEventsColl.findOne(query);
        return obj.toString();
	}

	public ArrayList<String> getAllControllers(){
	    ArrayList<String> toReturn = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("type", "controller");
        DBCursor cursor = rawEventsColl.find(query);
        DBObject data = null;
        List<ServiceEventRequest> list = new ArrayList<ServiceEventRequest>();
        while (cursor.hasNext()) {
            data = cursor.next();
            toReturn.add(data.toString());
        }
        return toReturn;
    }

    public ArrayList<String> getAllCrossroads(){
        ArrayList<String> toReturn = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("type", "crossroad");
        DBCursor cursor = rawEventsColl.find(query);
        DBObject data = null;
        List<ServiceEventRequest> list = new ArrayList<ServiceEventRequest>();
        while (cursor.hasNext()) {
            data = cursor.next();
            toReturn.add(data.toString());
        }
        return toReturn;
    }

    public GeneralInfo getGeneralInfo(){
		GeneralInfo gi = new GeneralInfo();
		int crossroadsCount = 0;
		int semaphoresCont = 0;

		//getting controllers
		int controllersCount = getAllControllers().size();

		//getting crossroads and semaphores
        BasicDBObject sems;
        ArrayList<String> toReturn = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("type", "crossroad");
        DBCursor cursor = rawEventsColl.find(query);
        DBObject data = null;
        List<ServiceEventRequest> list = new ArrayList<ServiceEventRequest>();
        while (cursor.hasNext()) {
            crossroadsCount ++;
            data = cursor.next();

            List<ServiceEventRequest> aaa = (List<ServiceEventRequest>) data.get("semaphores");
            semaphoresCont += aaa.size();
//            System.out.println(aaa);
//            System.out.println(data.get("semaphores").toString());
//            System.out.println("sems = " + sems);
        }




        gi.setnOfControllers(controllersCount);
        gi.setnOfCrossroads(crossroadsCount);
        gi.setnOfSemaphores(semaphoresCont);
		return gi;
	}

}





















