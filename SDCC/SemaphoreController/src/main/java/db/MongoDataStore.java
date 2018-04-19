/**
 * 
 */
package db;

import com.mongodb.*;
import com.mongodb.util.JSON;
import main.java.system.Printer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.UnknownHostException;
import java.util.ArrayList;
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
	public static DataStore getInstance(String mongoHost, int mongoPort)
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

	public Boolean storeRawEvent(String jsonData) {
		DBObject rawEvent = (DBObject) JSON.parse(jsonData);
		System.out.println("rawEvent: "+rawEvent);
		boolean success = false;

		System.out.println(rawEventsColl);

		if (rawEventsColl.insert(rawEvent) != null) {
			success = true;
		}
		else{
            rawEventsColl.remove(new BasicDBObject());
            rawEventsColl.insert(rawEvent);
        }

		printAllDocuments(rawEventsColl);
		return success;
	}

	@Override
	public List<ServiceEventRequest> getAll() {

		BasicDBObject query = new BasicDBObject();
		DBCursor cursor = rawEventsColl.find(query);
		DBObject data = null;
		List<ServiceEventRequest> list = new ArrayList<ServiceEventRequest>();
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

	@Override
	public Boolean updateController(String id, String crossroad) {

        DBObject listItem = new BasicDBObject("crossroads", new BasicDBObject("crossroadName",crossroad).append("semaphores", new ArrayList<>()));
        DBObject updateQuery = new BasicDBObject("$push", listItem);

        DBObject searchById = new BasicDBObject("_id", id);
        DBObject dbObj = rawEventsColl.findOne(searchById);

        rawEventsColl.update(dbObj, updateQuery);

//        printAllDocuments(rawEventsColl);
        return true;
	}

    public void printAllDocuments(DBCollection collection) {
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			Printer.getInstance().print(cursor.next().toString(), "green");
		}
	}

    @Override
	public void addFirstCrossroadToMongo(String id, String controllername) {
        controllerid = id;
        storeRawEvent("{'_id' : '" + id + "', " +
                "'controller' : '" +  id + "', " +
                "'crossroads' : [" +
                    "{ " +
                      "'crossroadName' : '" + controllername + "', " +
                      "'semaphores' : [" +
                        "{}" +
                       "] " +
                    "}" +
                "]}");

    }

    @Override
    public void addSemaphoreToMongo(String crossroadName, String semaphoreName) {

        DBObject listItem = new BasicDBObject("crossroads", new BasicDBObject("semaphores", new BasicDBObject("semaphore" , new BasicDBObject("semaphoreID", semaphoreName))));
        DBObject updateQuery = new BasicDBObject("$push", listItem);
//        DBObject updateQuery = new BasicDBObject("$push", "{ crossroads : [{ semaphores : [{ semaphore : { semaphoreID : test}}]}]}");
//        System.out.println(listItem);

        DBObject searchByCrossroad = new BasicDBObject("crossroadName", crossroadName);
        DBObject dbObj = rawEventsColl.findOne(searchByCrossroad);

        rawEventsColl.update(dbObj, updateQuery);

//        printAllDocuments(rawEventsColl);
    }


}
