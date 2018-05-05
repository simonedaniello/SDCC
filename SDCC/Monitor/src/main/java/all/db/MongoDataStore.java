
package all.db;

import com.mongodb.*;
import com.mongodb.util.JSON;
import main.java.FlinkResult;
import main.java.Semaphore;
import main.java.system.Printer;
import org.bson.BasicBSONObject;
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
	public static DataStore getInstance() throws UnknownHostException {
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
	public void writeListOfFlinkResultsOnDB(String entryType, ArrayList resultList) {

	    checkIfItExists(entryType);

		DBObject newEntry = new BasicDBObject();

        newEntry.put("_id", entryType);

        BasicDBList ranks = new BasicDBList();
        for(Object fr : resultList){
            BasicDBObject entry = new BasicDBObject("semId", ((FlinkResult) fr).getKey()).append("value", ((FlinkResult) fr).getValue());
            ranks.add(entry);
        }
        newEntry.put("ranking", ranks);


        rawEventsColl.insert(newEntry);

	}

	private void checkIfItExists(String objectName){
		DBObject searchById = new BasicDBObject("_id", objectName);
		DBObject dbObj = rawEventsColl.findOne(searchById);
		if (dbObj != null)
			rawEventsColl.remove(dbObj);
	}

}
