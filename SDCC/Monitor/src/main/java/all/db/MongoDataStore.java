
package all.db;

import all.front.FirstConsumer;
import com.mongodb.*;
import com.mongodb.util.JSON;
import entities.FlinkResult;
import entities.Semaphore;
import entities.system.Printer;
import org.bson.BasicBSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class MongoDataStore implements DataStore {

	private static MongoDataStore instance = new MongoDataStore();

	private static DBCollection rawEventsColl;
	public static final String COLLECTION_NAME = "events";

    private static String MONGO_HOST;
    private static int MONGO_PORT;

	private MongoDataStore() {
        System.out.println("initializing mongoDataStore \n\n\n\n");
        Properties properties = new Properties();
        String filename = "consumer.props";
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

        synchronized (MongoDataStore.class) {
            DB db;
            try {
                db = new MongoClient(MONGO_HOST,MONGO_PORT).getDB("stream");
                rawEventsColl = db.getCollection(COLLECTION_NAME);
                System.out.println("SUCCESS: connected to " + db.getName() + "\n\n\n\n");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
	}


	public static DataStore getInstance() {
		return instance;
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
    @Transactional
	public void writeListOfFlinkResultsOnDB(String entryType, ArrayList resultList) {

	    checkIfItExists(entryType);

		DBObject newEntry = new BasicDBObject();

        newEntry.put("_id", entryType);
        DBObject data;

        BasicDBList ranks = new BasicDBList();
        for(Object fr : resultList){
            BasicDBObject entry = new BasicDBObject("semId", ((FlinkResult) fr).getKey());
            entry.append("value", ((FlinkResult) fr).getValue());

            //{"semaphores.semaphoreID": "28e7234668777f9ed7a63b82eac501322fa9ac707238d8a3e9e89c599458ab13"}, {_id: 0, 'semaphores.$': 1}

			DBObject clause1 = new BasicDBObject("semaphores.semaphoreID", ((FlinkResult) fr).getKey());
			DBObject clause3 = new BasicDBObject("semaphores.$", 1);
			BasicDBList and = new BasicDBList();
			and.add(clause1);
			and.add(clause3);
			Printer.getInstance().print(and.toString() + "\n\n\n", "yellow");

            DBCursor cursor = rawEventsColl.find(clause1, clause3);
            while (cursor.hasNext()) {
                data = cursor.next();
                BasicDBList dblist = (BasicDBList) data.get("semaphores");
                BasicDBObject value = (BasicDBObject) dblist.get(0);
                Printer.getInstance().print(value.toString(), "cyan");
                entry.append("latitude", value.get("latitude"));
                entry.append("longitude", value.get("longitude"));
                entry.append("address", value.get("semaphoreStreet"));
                entry.append("malfunctions", value.get("malfunctions"));
            }

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
