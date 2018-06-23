package all.control;

import akka.dispatch.Mapper;
import all.db.MongoDataStore;
import all.entity.GeneralInfo;
import all.front.FirstProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class QuerySolver {

    public volatile String controllerResponse = null;

    private FirstProducer fp;
    private final int codeAddCrossroad = 603;
    private final int codeAddSemaphore = 602;
    private final int codeGetSituation = 601;
    public ArrayList<Semaphore> sems;

    private String myIP;
    private String input_ID;

    public QuerySolver(){

        Properties properties = new Properties();
        String filename = "monitorBackend.props";
        InputStream input = QuerySolver.class.getClassLoader().getResourceAsStream(filename);
        if (input == null){
            System.out.println("\n\n\n\n\nSorry, unable to find " + filename);
            return;
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myIP = properties.getProperty("INPUT_IP");
        input_ID = properties.getProperty("INPUT_PORT");



        this.fp = FirstProducer.getInstance();
    }

    /**
     * from: monitorBE
     * to: flinkDispatcher
     * aim: tell the flink dispatcher to add a crossroad in the right controller
     */
    public void addCrossroad(Crossroad crossroad){
        ArrayList<String> data = retrieveControllerFromMongo();
        Message m = new Message(input_ID, codeAddCrossroad);
        m.setIP(myIP);
        m.setCrossroad(crossroad);
        fp.sendMessage(data.get(0), m, data.get(1));
        waitForResponse();
        controllerResponse = null;
    }

    /**
     * from: monitorBE
     * to: flinkDispatcher
     * aim: tell the flink dispatcher to add a semaphore in the right position
     *
     */
    public void addSemaphore(Semaphore semaphore){
        ArrayList<String> data = retrieveSemaphoreFromMongo(semaphore);
        Message m = new Message("monitorBE", codeAddSemaphore);
        m.setIP(myIP);
        m.setSemaphore(semaphore);
        fp.sendMessage(data.get(0), m, data.get(1));
        waitForResponse();
        controllerResponse = null;
    }

    /**
     * from: monitorBE
     * to: DBController
     * aim: retrieve the general situation from mongoDB
     */
    public String getGeneralSituation(){

        try {
            GeneralInfo gi = MongoDataStore.getInstance().getGeneralInfo();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(gi);
        } catch (UnknownHostException | JsonProcessingException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * from: monitorBE
     * to: flinkDispatcher
     * aim: the flinkDispatcher tells the correct controller to send informations to the monitorBE
     */
    public String getCrossroadSituation(String crossroad){

//        ArrayList<String> data = retrieveCrossroadFromMongo(crossroad);
        Printer.getInstance().print("mi accingo a inviare il messaggio", "green");
        Message m = new Message("monitor", codeGetSituation);
        m.setIP(myIP);
        fp.sendMessage("address", m, crossroad);
        Printer.getInstance().print("messaggio inviato a " + crossroad + ", attendo la risposta", "yellow");
        String toReturn = waitForResponse();
        controllerResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String ritornare = null;
        try {
            ritornare = mapper.writeValueAsString(sems);
            Printer.getInstance().print(ritornare, "blue");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ritornare;
    }

    /**
     * from: monitorBE
     * to: DBController
     * aim: retrieve queries informations from mongoDB
     */
    public String getQueries(){
        return "to implement";
    }

    private String waitForResponse(){
        while(controllerResponse == null){}
        return controllerResponse;
    }

    //TODO
    private ArrayList<String> retrieveControllerFromMongo() {
        //data[0] = controller ip
        //data[1] = controller topic
        return null;
    }

    //TODO
    private ArrayList<String> retrieveSemaphoreFromMongo(Semaphore semaphore) {
        //data[0] = controller ip
        //data[1] = controller topic
        return null;
    }

    //TODO
    private ArrayList<String> retrieveCrossroadFromMongo(Crossroad crossroad) {
        //data[0] = controller ip
        //data[1] = controller topic
        return null;
    }



}
