package all.control;

import all.front.FirstProducer;
import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;

import java.util.ArrayList;

public class QuerySolver {

    public String controllerResponse = null;

    private FirstProducer fp;
    private final String myIP = "localhost";
    private final int codeAddCrossroadd = 601;
    private final int codeAddSemaphore = 602;
    private final int codeGetSituation = 603;

    public QuerySolver(){
        this.fp = FirstProducer.getInstance();
    }

    /**
     * from: monitorBE
     * to: flinkDispatcher
     * aim: tell the flink dispatcher to add a crossroad in the right controller
     */
    public void addCrossroad(Crossroad crossroad){
        ArrayList<String> data = retrieveControllerFromMongo();
        Message m = new Message("monitorBE", codeAddCrossroadd);
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

        return "to implement";
    }

    /**
     * from: monitorBE
     * to: flinkDispatcher
     * aim: the flinkDispatcher tells the correct controller to send informations to the monitorBE
     */
    public String getCrossroadSituation(Crossroad crossroad){
        ArrayList<String> data = retrieveCrossroadFromMongo(crossroad);

        Message m = new Message("monitorBE", codeGetSituation);
        m.setIP(myIP);
        m.setCrossroad(crossroad);
        fp.sendMessage(data.get(0), m, data.get(1));
        String toReturn = waitForResponse();
        controllerResponse = null;
        return toReturn;
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
