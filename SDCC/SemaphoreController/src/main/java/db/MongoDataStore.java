/**
 * 
 */
package db;

import com.mongodb.*;
import com.mongodb.util.JSON;
import entities.Semaphore;
import entities.system.Printer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class MongoDataStore implements DataStore {

	private static DataStore mongoDataStore;
	private static DBCollection rawEventsColl;
	public static final String COLLECTION_NAME = "events";
	private String controllerid = null;

	private static String MONGO_HOST;
	private static int MONGO_PORT;

	@Autowired
	private MongoDataStore() {

		Properties properties = new Properties();
		String filename = "controllerConfiguration.props";
		InputStream input = MongoDataStore.class.getClassLoader().getResourceAsStream(filename);
		if (input == null){
			System.out.println("\n\n\n\n\nSorry, unable to find " + filename);
			return;
		}
		try {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MONGO_HOST = properties.getProperty("MONGO_HOST");
		MONGO_PORT = Integer.valueOf(properties.getProperty("MONGO_PORT"));
		/*System.out.println(MONGO_HOST);
		System.out.println(MONGO_PORT);*/

	}

	public static DataStore getInstance() throws UnknownHostException {
		synchronized (MongoDataStore.class) {
			if (mongoDataStore == null) {
                mongoDataStore = new MongoDataStore();
                DB db = new MongoClient(MONGO_HOST,MONGO_PORT).getDB("stream");
				rawEventsColl = db.getCollection("events");
			}
		}
		return mongoDataStore;
	}

	public Boolean storeRawEvent(String jsonData) {
		DBObject rawEvent = (DBObject) JSON.parse(jsonData);
/*
		System.out.println("rawEvent: "+rawEvent);
*/
		boolean success = false;

/*
		System.out.println(rawEventsColl);
*/

		if (rawEventsColl.insert(rawEvent) != null) {
			success = true;
		}
		else{
            rawEventsColl.remove(new BasicDBObject());
            rawEventsColl.insert(rawEvent);
        }

//		printAllDocuments(rawEventsColl);
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
//		System.out.println("PRINT data after while::" + list);
		return list;
	}

    /**
     * add a crossroad to a controller and create the crossroad object in the db
     * @param id
     * @param crossroad
     * @return
     */
	@Override
	public Boolean updateController(String id, String crossroad, String street) {
		checkIfItExists(crossroad);

        DBObject listItem = new BasicDBObject("crossroads", new BasicDBObject("crossroadName",crossroad));
        DBObject updateQuery = new BasicDBObject("$push", listItem);

        DBObject searchById = new BasicDBObject("_id", id);
        DBObject dbObj = rawEventsColl.findOne(searchById);

        rawEventsColl.update(dbObj, updateQuery);

        storeRawEvent("{'_id' : '" + crossroad + "', " +
                "'crossroadID' : '" +  crossroad + "', " +
				"'type': 'crossroad', " +
                "'street' : '" + street +"', " +
                "'semaphores' : [" +
                "]}");


//        printAllDocuments(rawEventsColl);
        return true;
	}

    /**
     * create the controller, add the first crossroad to the controller
     * @param id
     * @param crossroadName
     */
    @Override
	public void addFirstCrossroadToMongo(String id, String crossroadName, String street) {
    	checkIfItExists(id);
    	checkIfItExists(crossroadName);
        controllerid = id;
        storeRawEvent("{'_id' : '" + id + "', " +
                "'controller' : '" +  id + "', " +
				"'type' : 'controller', " +
                "'crossroads' : [" +
                    "{ " +
                      "'crossroadName' : '" + crossroadName + "', " +
                    "}" +
                "]}");

		storeRawEvent("{'_id' : '" + crossroadName + "', " +
				"'crossroadID' : '" +  crossroadName + "', " +
                "'street' : '" + street +"', " +
                "'type': 'crossroad', " +
				"'semaphores' : [" +
				"]}");

    }

    @Override
    public void addSemaphoreToMongo(String crossroadName, Semaphore semaphore) {

        BasicDBObject o = new BasicDBObject("semaphoreID", semaphore.getID());
        o.append("semaphoreStreet", semaphore.getStreet());
        o.append("malfunctions", semaphore.getMalfunctions());
        o.append("latitude", semaphore.getLatitude());
        o.append("longitude", semaphore.getLongitude());
        o.append("malfunctions", 0);
		DBObject listItem = new BasicDBObject("semaphores", o);
        DBObject updateQuery = new BasicDBObject("$push", listItem);

        DBObject searchByCrossroad = new BasicDBObject("_id", crossroadName);
        DBObject dbObj = rawEventsColl.findOne(searchByCrossroad);

//        System.out.println(dbObj);

        rawEventsColl.update( dbObj, updateQuery);

    }

	@Override
	public void addMalfunctionToDB(String semaphoreid) {
		int currentMalfunction;

		//searching for the object containing the semaphoreID
        DBObject clause1 = new BasicDBObject("semaphores.semaphoreID", semaphoreid);
		DBObject clause3 = new BasicDBObject("semaphores.$", 1);
		DBCursor cursor = rawEventsColl.find(clause1, clause3);
        while (cursor.hasNext()) {
            DBObject data = cursor.next();

            //getting the semaphore
            BasicDBList dblist = (BasicDBList) data.get("semaphores");
            BasicDBObject value = (BasicDBObject) dblist.get(0);

            //modifying the number of malfunctions
			currentMalfunction = (int) value.get("malfunctions");

			//querying for the update
			BasicDBObject query = new BasicDBObject();
			query.put("_id", data.get("_id"));
			query.put("semaphores.semaphoreID", value.get("semaphoreID"));
			BasicDBObject d = new BasicDBObject();
			d.put("semaphores.$.malfunctions", currentMalfunction + 1);
			BasicDBObject command = new BasicDBObject();
			command.put("$set", d);
			rawEventsColl.update(query, command);
        }

	}

	public void printAllDocuments(DBCollection collection) {
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
/*
            Printer.getInstance().print(cursor.next().toString(), "green");
*/
        }
    }

    private void checkIfItExists(String objectName){
		DBObject searchById = new BasicDBObject("_id", objectName);
		DBObject dbObj = rawEventsColl.findOne(searchById);
		if (dbObj != null)
			rawEventsColl.remove(dbObj);
	}

}
