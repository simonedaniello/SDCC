package all.master;


import all.controllers.CrossroadController;
import com.fasterxml.jackson.core.JsonProcessingException;
import db.MongoDataStore;
import main.java.Semaphore;
import main.java.system.Printer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class Coordinator {

    private static Coordinator instance = new Coordinator();
    private ArrayList<String> crossroadControllerlist = new ArrayList<>();
    private String idInMongo = null;
    private String id;

    private Coordinator(){
        Properties properties = new Properties();
        String filename = "controllerConfiguration.props";
        InputStream input = Semaphore.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("\n\nSorry, unable to find " + filename);
            return;
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        id = properties.getProperty("coordinatorid");
    }

    public static Coordinator getInstance() {
        return instance;
    }

    public void addCrossroadController(String crossroadControllerID, String crossroadControllerAddress){

        Thread thread1 = new Thread(() -> new CrossroadController(crossroadControllerID, crossroadControllerAddress));

        thread1.start();

        try {
            if(idInMongo == null) {
                idInMongo = id;
                MongoDataStore.getInstance().addFirstCrossroadToMongo(id, crossroadControllerID, crossroadControllerAddress);
            }
            else{
                System.out.println("faccio l'update");
                MongoDataStore.getInstance().updateController(id, crossroadControllerID, crossroadControllerAddress);
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }


}
