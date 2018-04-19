package all.master;


import all.controllers.CrossroadController;
import com.fasterxml.jackson.core.JsonProcessingException;
import db.MongoDataStore;
import main.java.system.Printer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Coordinator {

    private static Coordinator instance = new Coordinator();
    private ArrayList<String> crossroadControllerlist = new ArrayList<>();
    private static final String MONGO_HOST = "localhost";
    private static final int MONGO_PORT = 27017;
    private String idInMongo = null;
    private String id = "cambiaAssolutamente";

    private Coordinator(){}

    public static Coordinator getInstance() {
        return instance;
    }

    public void addCrossroadController(String crossroadControllerID, String crossroadControllerAddress){

        Thread thread1 = new Thread(() -> new CrossroadController(crossroadControllerID, crossroadControllerAddress));

        thread1.start();

        try {
            if(idInMongo == null) {
                idInMongo = id;
                MongoDataStore.getInstance(MONGO_HOST, MONGO_PORT).addFirstCrossroadToMongo(id, crossroadControllerID);
            }
            else{
                System.out.println("faccio l'update");
                MongoDataStore.getInstance(MONGO_HOST, MONGO_PORT).updateController(id, crossroadControllerID);

                MongoDataStore.getInstance(MONGO_HOST, MONGO_PORT).addSemaphoreToMongo(crossroadControllerID, "test");

            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }

}
