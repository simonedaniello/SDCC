package all.controllers;


import all.db.MongoDataStore;
import all.front.FirstConsumer;
import entities.FlinkResult;
import entities.system.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private ArrayList<FlinkResult> averageSpeedList1hour = new ArrayList<>();
    private ArrayList<FlinkResult> averageSpeedList24hours = new ArrayList<>();

    private List<FlinkResult> oldAverageSpeedList= new ArrayList<>();
    private ArrayList<FlinkResult> oldAverageSpeedList1hour= new ArrayList<>();
    private ArrayList<FlinkResult> oldAverageSpeedList24hours= new ArrayList<>();

    private ArrayList<FlinkResult> quantileList = new ArrayList<>();
    private ArrayList<FlinkResult> quantileList1hour = new ArrayList<>();
    private ArrayList<FlinkResult> quantileList24hours = new ArrayList<>();

    private List<FlinkResult> oldQuantileList = new ArrayList<>();
    private ArrayList<FlinkResult> oldQuantileList1hour = new ArrayList<>();
    private ArrayList<FlinkResult> oldQuantileList24hours = new ArrayList<>();

    private ArrayList<FlinkResult> IoTSpeedList = new ArrayList<>();
    private ArrayList<FlinkResult> IoTSpeedList1hour = new ArrayList<>();
    private ArrayList<FlinkResult> IoTSpeedList24hours = new ArrayList<>();

    private List<FlinkResult> oldIoTList = new ArrayList<>();
    private ArrayList<FlinkResult> oldIoTList1hour = new ArrayList<>();
    private ArrayList<FlinkResult> oldIoTList24hours = new ArrayList<>();



    public Monitorer(){

        FirstConsumer fc = FirstConsumer.getInstance();
        fc.setMonitorer(this, new FlinkStatusMonitorer());
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
    public void addAvgFromKafka15(FlinkResult f){
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
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething)
                averageSpeedList.add(f);
        }
    }

    public void addAvgFromKafka1(FlinkResult f){
        if(f.getNumberOfCars() != 0) {
            boolean IhaveDoneSomething = false;
            String idToAdd = f.getKey();
            for (FlinkResult inList : averageSpeedList1hour) {
                if (inList.getKey().equals(idToAdd)) {
                    double total = f.getNumberOfCars() + inList.getNumberOfCars();
                    double inListMultiplier = inList.getNumberOfCars() / total;
                    double fmultiplier = f.getNumberOfCars()/total;
                    inList.setValue(inList.getValue() * inListMultiplier + f.getValue() * fmultiplier);
                    inList.setNumberOfCars((int) total);
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething) {
                averageSpeedList1hour.add(f);
            }

        }
    }

    public void addAvgFromKafka24(FlinkResult f){
        if(f.getNumberOfCars() != 0) {
            boolean IhaveDoneSomething = false;
            String idToAdd = f.getKey();
            for (FlinkResult inList : averageSpeedList24hours) {
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
                averageSpeedList24hours.add(f);
//                Printer.getInstance().print("\n\naggiungo il semaforo "+ f.getKey() + " con valore " + f.getValue(), "yellow");
            }

        }
    }


    public void addQuantilFromKafka15(FlinkResult f){
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

    public void addQuantilFromKafka1(FlinkResult f){
        if(f.getNumberOfCars() != 0) {
            boolean IhaveDoneSomething = false;
            String idToAdd = f.getKey();
            for (FlinkResult inList : quantileList1hour) {
                if (inList.getKey().equals(idToAdd)) {
                    double total = f.getNumberOfCars() + inList.getNumberOfCars();
                    inList.setValue(inList.getValue() * (inList.getNumberOfCars() / total) + f.getValue() * (f.getNumberOfCars() / total));
                    inList.setNumberOfCars((int) total);
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething)
                quantileList1hour.add(f);
        }
    }

    public void addQuantilFromKafka24(FlinkResult f){
        if(f.getNumberOfCars() != 0) {
            boolean IhaveDoneSomething = false;
            String idToAdd = f.getKey();
            for (FlinkResult inList : quantileList24hours) {
                if (inList.getKey().equals(idToAdd)) {
                    double total = f.getNumberOfCars() + inList.getNumberOfCars();
                    inList.setValue(inList.getValue() * (inList.getNumberOfCars() / total) + f.getValue() * (f.getNumberOfCars() / total));
                    inList.setNumberOfCars((int) total);
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething)
                quantileList24hours.add(f);
        }
    }


    public void addQuery3FromKafka15(FlinkResult f){

    }

    public void addQuery3FromKafka1(FlinkResult f){

    }

    public void addQuery3FromKafka24(FlinkResult f){

    }


    /**
     * 0 : mode 15 minutes average speed list
     * 1: mode 1 hour average average speed list
     * 2: mode 24 hours average speed list
     */
    private void saveDataOnMongo15min(){
        if(averageSpeedList.size() < 10)
            oldAverageSpeedList = averageSpeedList;
        else
            oldAverageSpeedList =  averageSpeedList.subList(0, 9);

        oldQuantileList = quantileList;

        if(oldAverageSpeedList.size() != 0)
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query15averageSpeed", oldAverageSpeedList);
        if(oldQuantileList.size() != 0)
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query15quantileSpeed", oldQuantileList);

    }

    private void saveDataOnMongo1hour(){
        if(averageSpeedList1hour.size() < 10)
            oldAverageSpeedList1hour = averageSpeedList1hour;
        else
            oldAverageSpeedList1hour = (ArrayList<FlinkResult>) averageSpeedList1hour.subList(0, 9);

        if(quantileList1hour.size()<10)
            oldQuantileList1hour = quantileList1hour;
        else
            oldQuantileList1hour = (ArrayList<FlinkResult>) quantileList1hour.subList(0, 9);

        if(oldAverageSpeedList1hour.size() != 0)
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query1averageSpeed", oldAverageSpeedList1hour);
        if(oldQuantileList1hour.size() != 0)
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query1quantileSpeed", oldQuantileList1hour);
    }

    private void saveDataOnMongo24hours(){
        if(averageSpeedList24hours.size() < 10)
            oldAverageSpeedList24hours = averageSpeedList24hours;
        else
            oldAverageSpeedList24hours = (ArrayList<FlinkResult>) averageSpeedList24hours.subList(0, 9);

        if(quantileList24hours.size()<10)
            oldQuantileList24hours = quantileList24hours;
        else
            oldQuantileList24hours = (ArrayList<FlinkResult>) quantileList24hours.subList(0, 9);

        if(oldAverageSpeedList24hours.size() != 0)
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query24averageSpeed", oldAverageSpeedList24hours);
        if(oldQuantileList24hours.size() != 0)
            MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query24quantileSpeed", oldQuantileList24hours);
    }

    private void calculateRanking(){

        PSquared pSquared = new PSquared(0.5f);
        if(averageSpeedList.size() != 0){
                averageSpeedList.sort((e1, e2) -> (e2.getValue() > e1.getValue()) ? 1 : -1);
        }

        if(quantileList.size() != 0){

            for (FlinkResult f: quantileList
                 ) {
                pSquared.accept((float) f.getValue());
            }

            float globalMedian = pSquared.getPValue();
            System.out.println("GLOBAL MEDIAN IS: " + globalMedian);

            ArrayList<FlinkResult> toremove = new ArrayList<>();

            for (FlinkResult f: quantileList
                    ) {
                System.out.println(f.getValue());
                if (f.getValue() < globalMedian){
                    toremove.add(f);
                    }
            }
            quantileList.removeAll(toremove);
            quantileList.sort((e1, e2) -> (e2.getValue() > e1.getValue()) ? 1 : -1);
        }

        saveDataOnMongo15min();
        saveDataOnMongo1hour();
        saveDataOnMongo24hours();
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

        averageSpeedList.clear();
        averageSpeedList1hour.clear();
        averageSpeedList24hours.clear();

        quantileList.clear();
        quantileList1hour.clear();
        quantileList24hours.clear();
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
