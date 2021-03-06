package all.controllers;



// TODO MONITORER CLASS
// TODO dato che il semaforo non fa nulla per la maggior parte del tempo, implementiamo sul semaforo una chiamata sema REST
// TODO e facciamo comunicare il front direttamente con il semaforo in questione per sapere la situazione sul traffico


import all.db.MongoDataStore;
import all.front.FirstConsumer;
import all.front.FirstProducer;
import entities.FlinkResult;
import entities.Message;
import entities.system.Printer;

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
    private PSquared pSquared = new PSquared(0.5f);

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
//                    Printer.getInstance().print("ho settato value a " + inList.getValue() + " e numero macchine a " + inList.getNumberOfCars(), "yellow");
                    IhaveDoneSomething = true;
                    break;
                }
            }
            if (!IhaveDoneSomething) {
                averageSpeedList1hour.add(f);
//                Printer.getInstance().print("\n\naggiungo il semaforo "+ f.getKey() + " con valore " + f.getValue(), "yellow");
            }

        }
        else
            Printer.getInstance().print("Non aggiungo perchè 0", "red");
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
        else
            Printer.getInstance().print("Non aggiungo perchè 0", "red");
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
    private void saveDataOnMongo15min(){
        if(averageSpeedList.size() < 40)
            oldAverageSpeedList = averageSpeedList;
        else
            oldAverageSpeedList =  averageSpeedList.subList(0, 39);

        if(quantileList.size()<40)
            oldQuantileList = quantileList;
        else
            oldQuantileList = quantileList.subList(0, 39);

        MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query15averageSpeed", oldAverageSpeedList);
        MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query15quantileSpeed", oldQuantileList);

    }

    private void saveDataOnMongo1hour(){
        if(averageSpeedList1hour.size() < 40)
            oldAverageSpeedList1hour = averageSpeedList1hour;
        else
            oldAverageSpeedList1hour = (ArrayList<FlinkResult>) averageSpeedList1hour.subList(0, 39);

        if(quantileList1hour.size()<40)
            oldQuantileList1hour = quantileList1hour;
        else
            oldQuantileList1hour = (ArrayList<FlinkResult>) quantileList1hour.subList(0, 39);

        MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query1averageSpeed", oldAverageSpeedList1hour);
        MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query1quantileSpeed", oldQuantileList1hour);
    }

    private void saveDataOnMongo24hours(){
        if(averageSpeedList24hours.size() < 40)
            oldAverageSpeedList24hours = averageSpeedList24hours;
        else
            oldAverageSpeedList24hours = (ArrayList<FlinkResult>) averageSpeedList24hours.subList(0, 39);

        if(quantileList24hours.size()<40)
            oldQuantileList24hours = quantileList24hours;
        else
            oldQuantileList24hours = (ArrayList<FlinkResult>) quantileList24hours.subList(0, 39);

        MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query24averageSpeed", oldAverageSpeedList24hours);
        MongoDataStore.getInstance().writeListOfFlinkResultsOnDB("query24quantileSpeed", oldQuantileList24hours);
    }

    private void calculateRanking(){
        if(averageSpeedList.size() != 0){
                averageSpeedList.sort((e1, e2) -> (e2.getValue() > e1.getValue()) ? 1 : -1);
                saveDataOnMongo15min();
                saveDataOnMongo1hour();
                saveDataOnMongo24hours();
            }

            if(quantileList.size() != 0){

                for (FlinkResult f: quantileList
                     ) {
                    pSquared.accept(f.getNumberOfCars());
                }

                float globalMedian = pSquared.getPValue();

                for (FlinkResult f: quantileList
                        ) {
                    if (f.getNumberOfCars()<globalMedian)
                        quantileList.remove(f);
                }


                quantileList.sort((e1, e2) -> (e2.getValue() > e1.getValue()) ? 1 : -1);

                saveDataOnMongo15min();
                saveDataOnMongo1hour();
                saveDataOnMongo24hours();
            }
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

//        Message m1 = new Message("monitorer", 711);
//        m1.setPartialRanking(averageSpeedList);
//        FirstProducer.getInstance().sendMessage("address", m1, "ranking");
        averageSpeedList.clear();
        averageSpeedList1hour.clear();
        averageSpeedList24hours.clear();


//        Message m2 = new Message("monitorer", 712);
//        m2.setPartialRanking(quantileList);
//        FirstProducer.getInstance().sendMessage("address", m2, "ranking");
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
