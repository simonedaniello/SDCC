package all.master;


import all.controllers.CrossroadController;
import all.telegramBOT.TelegramBotStarter;
import db.MongoDataStore;
import entities.Semaphore;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class Coordinator {

    private static Coordinator instance = new Coordinator();
    private ArrayList<CrossroadController> crossroadControllerlist = new ArrayList<>();
    private String idInMongo = null;
    private String id;
    TelegramBotStarter bot;

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

        bot = new TelegramBotStarter();
    }

    public static Coordinator getInstance() {
        return instance;
    }

    public void addCrossroadController(String crossroadControllerID, String crossroadControllerAddress){

        Thread thread1 = new Thread(() -> crossroadControllerlist.add(new CrossroadController(crossroadControllerID, crossroadControllerAddress, bot)));

        thread1.start();

        try {
            if(idInMongo == null) {
                idInMongo = id;
                MongoDataStore.getInstance().addFirstCrossroadToMongo(id, crossroadControllerID, crossroadControllerAddress);
            }
            else{
                MongoDataStore.getInstance().updateController(id, crossroadControllerID, crossroadControllerAddress);
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }

    public void updateChatIdList(long chat_id) {
        for(CrossroadController c : crossroadControllerlist){
            c.addChatID(chat_id);
        }
    }
}
