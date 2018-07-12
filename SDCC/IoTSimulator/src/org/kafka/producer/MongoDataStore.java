/**
 * 
 */
package org.kafka.producer;

import com.mongodb.*;
import com.mongodb.util.JSON;

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
    private static String MONGO_HOST;
    private static int MONGO_PORT;

	//private MongoTemplate mongoTemplate;

	private MongoDataStore() {

        Properties properties = new Properties();
        String filename = "consumer.props";
        InputStream input = MongoDataStore.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
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

    }

	/**
	 * 
	 * @param mongoHost
	 * @param mongoPort
	 * @return
	 * @throws UnknownHostException
	 */



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

	public boolean storeRawEvent(String jsonData) {
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

/*
			System.out.println("PRINT data::" + data);
*/
			ServiceEventRequest request = new ServiceEventRequest();
			request.setName((String) data.get("name"));
			list.add(request);
		}
/*
		System.out.println("PRINT data after while::" + list);
*/
		return list;
	}

}
