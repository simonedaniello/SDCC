/**
 * 
 */
package all.db;

import all.entity.GeneralInfo;
import all.front.FirstConsumer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.util.JSON;
import main.java.FlinkResult;
import main.java.Semaphore;
import main.java.system.Printer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class MongoDataStore implements DataStore {

	private static DataStore mongoDataStore;
	private static DBCollection rawEventsColl;

    private static String MONGO_HOST;
    private static int MONGO_PORT;

	@Autowired
	private MongoDataStore() {
        Properties properties = new Properties();
        String filename = "monitorBackend.props";
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


	}

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
		BasicDBObject query = new BasicDBObject("type", "crossroad");
        DBCursor cursor = rawEventsColl.find(query);
        DBObject data;
		while (cursor.hasNext()) {
            crossroadsCount ++;
            data = cursor.next();
            List<Semaphore> aaa = (List<Semaphore>) data.get("semaphores");
            semaphoresCont += aaa.size();
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        //getting query 1-15 minutes
		ArrayList<FlinkResult> query115 = new ArrayList<>();
		BasicDBObject quey115minutes = new BasicDBObject("_id", "query15averageSpeed");
		cursor = rawEventsColl.find(quey115minutes);
		while (cursor.hasNext()) {
			data = cursor.next();
            List<FlinkResult> queryResults = (List<FlinkResult>) data.get("ranking");
            query115.addAll(queryResults);
		}

        //getting query 1-1 hour
        ArrayList<FlinkResult> query11 = new ArrayList<>();
        BasicDBObject quey11hour = new BasicDBObject("_id", "query1averageSpeed");
        cursor = rawEventsColl.find(quey11hour);
        while (cursor.hasNext()) {
            data = cursor.next();
            List<FlinkResult> queryResults = (List<FlinkResult>) data.get("ranking");
            query11.addAll(queryResults);
        }

        //getting query 1-24 hours
        ArrayList<FlinkResult> query124 = new ArrayList<>();
        BasicDBObject quey124hours = new BasicDBObject("_id", "query24averageSpeed");
        cursor = rawEventsColl.find(quey124hours);
        while (cursor.hasNext()) {
            data = cursor.next();
            List<FlinkResult> queryResults = (List<FlinkResult>) data.get("ranking");
            query124.addAll(queryResults);
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        //getting query 2-15 minutes
        ArrayList<FlinkResult> query215 = new ArrayList<>();
        BasicDBObject query215minutes = new BasicDBObject("_id", "query15quantileSpeed");
        cursor = rawEventsColl.find(query215minutes);
        while (cursor.hasNext()) {
            data = cursor.next();
            List<FlinkResult> queryResults = (List<FlinkResult>) data.get("ranking");
            query215.addAll(queryResults);
        }

        //getting query 2-1 hour
        ArrayList<FlinkResult> query21 = new ArrayList<>();
        BasicDBObject query21hour = new BasicDBObject("_id", "query1quantileSpeed");
        cursor = rawEventsColl.find(query21hour);
        while (cursor.hasNext()) {
            data = cursor.next();
            List<FlinkResult> queryResults = (List<FlinkResult>) data.get("ranking");
            query21.addAll(queryResults);
        }

        //getting query 2-24 hours
        ArrayList<FlinkResult> query224 = new ArrayList<>();
        BasicDBObject query224hours = new BasicDBObject("_id", "query24quantileSpeed");
        cursor = rawEventsColl.find(query224hours);
        while (cursor.hasNext()) {
            data = cursor.next();
            List<FlinkResult> queryResults = (List<FlinkResult>) data.get("ranking");
            query224.addAll(queryResults);
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


        gi.setnOfControllers(controllersCount);
        gi.setnOfCrossroads(crossroadsCount);
        gi.setnOfSemaphores(semaphoresCont);

        gi.setQuery1(query115);
        gi.setQuery11hour(query11);
        gi.setQuery124hours(query124);

        gi.setQuery2(query215);
        gi.setQuery21hour(query21);
        gi.setQuery224hours(query224);


        Printer.getInstance().print(gi.getQuery1().toString(), "yellow");
		return gi;
	}

}





















