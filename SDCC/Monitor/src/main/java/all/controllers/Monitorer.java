package all.controllers;



// TODO MONITORER CLASS
// TODO dato che il semaforo non fa nulla per la maggior parte del tempo, implementiamo sul semaforo una chiamata sema REST
// TODO e facciamo comunicare il front direttamente con il semaforo in questione per sapere la situazione sul traffico


import all.db.MongoDataStore;
import all.front.FirstConsumer;
import all.front.FirstProducer;
import main.java.FlinkResult;
import main.java.Message;
import main.java.system.Printer;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 *
 * Format Message used:
 *      Class name: MessageMonitor
 *      Fields:
 *          int Code
 *          ArrayList<String> tenIntersectionFifteen            //query1
 *          ArrayList<String> tenIntersectionOneHour            //query1
 *          ArrayList<String> tenIntersectionOneDay             //query1
 *          ArrayList<String> medianTooHighFifteen              //query2
 *          ArrayList<String> medianTooHighOneHour              //query2
 *          ArrayList<String> medianTooHighOneDay               //query2
 *          ArrayList<String> tooMuchCongestion                 //query3
 *          ArrayList<Semaphore> semaphoresIdWithMalfunctions   //monitoring
 *          float throughput                                    //monitoring
 *          float latency                                       //monitoring
 *
 *
 */
public class Monitorer {

    private ArrayList<FlinkResult> averageSpeedList = new ArrayList<>();
    private ArrayList<FlinkResult> oldAverageSpeedList= new ArrayList<>();

    private ArrayList<FlinkResult> quantileList = new ArrayList<>();
    private ArrayList<FlinkResult> oldQuantileList = new ArrayList<>();

    public Monitorer(){

        FirstConsumer fc = FirstConsumer.getInstance();
        fc.setMonitorer(this);
        try {
            fc.subscribeToTopic("monitorer");

            Thread thread1 = new Thread(() -> {
                System.out.println("starting kafka consumer...");
                fc.runConsumer();
            });

            thread1.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 20000, 20000); // every 20 seconds, after 20 seconds

    }

    /**
     * Retrieving data from kafka channel.
     * With this function the monitor listens on every topic and call the correct function
     */
    public void addAvgFromKafka(FlinkResult f){
        if(f.getNumberOfCars() != 0) {
            boolean IhaveDoneSomething = false;
            String idToAdd = f.getKey();
            for (FlinkResult inList : averageSpeedList) {
                if (inList.getKey().equals(idToAdd)) {
                    double total = f.getNumberOfCars() + inList.getNumberOfCars();
                    double inListMultiplier = inList.getNumberOfCars() / total;
                    double fmultiplier = f.getNumberOfCars()/total;
                    inList.setValue(inList.getValue() * inListMultiplier + f.getValue() * fmultiplier);
                    inList.setNumberOfCars((int) total);
//                    Printer.getInstance().print("ho settato value a " + inList.getValue() + " e numero macchine a " + inList.getNumberOfCars(), "yellow");
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething) {
                averageSpeedList.add(f);
//                Printer.getInstance().print("\n\naggiungo il semaforo "+ f.getKey() + " con valore " + f.getValue(), "yellow");
            }

        }
        else
            Printer.getInstance().print("Non aggiungo perchè 0", "red");
    }

    public void addQuantilFromKafka(FlinkResult f){
        if(f.getNumberOfCars() != 0) {
            boolean IhaveDoneSomething = false;
            String idToAdd = f.getKey();
            for (FlinkResult inList : quantileList) {
                if (inList.getKey().equals(idToAdd)) {
                    double total = f.getNumberOfCars() + inList.getNumberOfCars();
                    inList.setValue(inList.getValue() * (inList.getNumberOfCars() / total) + f.getValue() * (f.getNumberOfCars() / total));
                    inList.setNumberOfCars((int) total);
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething)
                quantileList.add(f);
        }
    }

    /**
    * Work with data from query1
    */
    public void receivedQuery1(){

    }

    /**
     * Work with data from query2
     */
    public void receivedQuery2(){

    }

    /**
     * Work with data from query3
     */
    public void receivedQuery3(){

    }

    /**
     * Signal a semaphore malfunction
     */
    public void receivedSemaphoresMalfunction(){

    }

    /**
     * 0 : mode 15 minutes average speed list
     * 1: mode 1 hour average average speed list
     * 2: mode 24 hours average speed list
     */
    public void saveDataOnMongo15min(){
        if(averageSpeedList.size() < 40)
            oldAverageSpeedList = averageSpeedList;
        else
            oldAverageSpeedList = (ArrayList<FlinkResult>) averageSpeedList.subList(0, 39);

        if(quantileList.size()<40)
            oldQuantileList = quantileList;
        else
            oldQuantileList = (ArrayList<FlinkResult>) quantileList.subList(0, 39);

        try {
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("15averageSpeed", oldAverageSpeedList);
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("15quantileSpeed", oldQuantileList);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    private void calculateRanking(){
        averageSpeedList.sort((e1, e2) -> (e2.getValue() > e1.getValue()) ? 1 : -1);
        quantileList.sort((e1, e2) -> (e2.getValue() > e1.getValue()) ? 1 : -1);
        saveDataOnMongo15min();
    }

    private class TimerClass extends TimerTask{
        @Override
        public void run() {
            calculateRanking();
            sendRanking();
        }
    }

    private void sendRanking(){

        printRankings();

        Message m1 = new Message("monitorer", 711);
        m1.setPartialRanking(averageSpeedList);
        FirstProducer.getInstance().sendMessage("address", m1, "ranking");
        averageSpeedList.clear();


        Message m2 = new Message("monitorer", 712);
        m2.setPartialRanking(quantileList);
        FirstProducer.getInstance().sendMessage("address", m2, "ranking");
        quantileList.clear();
    }

    private void printRankings(){
        Printer.getInstance().print("AVERAGE SPEED RANKING", "yellow");
        for(FlinkResult f : averageSpeedList){
            Printer.getInstance().print("\tkey: " + f.getKey() + ", value: " + f.getValue() + ", count: " + f.getNumberOfCars(), "blue");
        }
        Printer.getInstance().print("QUANTILE RANKING", "yellow");
        for(FlinkResult f : quantileList){
            Printer.getInstance().print("\tkey: " + f.getKey() + ", value: " + f.getValue() + ", count: " + f.getNumberOfCars(), "blue");
        }
    }


}
