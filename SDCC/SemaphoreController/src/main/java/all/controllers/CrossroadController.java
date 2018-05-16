package all.controllers;

import all.front.FirstConsumer;
import all.front.FirstProducer;
import all.telegramBOT.TelegramBotStarter;
import db.MongoDataStore;
import main.java.Crossroad;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class CrossroadController{


    private Crossroad crossroad;
    private TwoPCController twopc;
    private FirstConsumer fc;
    private FirstProducer fp;
    private Monitorer monitorer;
    private TelegramBotStarter telegramBot;
    private ArrayList<Long> chatids = new ArrayList<>();
    private boolean thereIsaMalfunction = false;


    public CrossroadController(String ID, String address, TelegramBotStarter bot){

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
        int slottimeinseconds = Integer.parseInt(properties.getProperty("slottimeinseconds"));

        this.telegramBot = bot;
        this.crossroad = new Crossroad(ID, address);
        this.fp = new FirstProducer();
        this.twopc = new TwoPCController(fp);
        this.monitorer = new Monitorer(fp);
        this.fc = new FirstConsumer(twopc, monitorer);



        fc.setController(this);
        monitorer.setCrossroad(crossroad);
        try {
            fc.subscribeToTopic("monitor");
            fc.subscribeToTopic(crossroad.getID());

            Thread thread1 = new Thread(() -> {
                System.out.println("starting kafka consumer...");
                fc.runConsumer();
            });

            thread1.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerClass(), 5000, slottimeinseconds*1000);
    }

    public ArrayList<Semaphore> getSemaphoreInCrossroad(){
        return crossroad.getSemaphores();
    }

    public void addSemaphore(Semaphore semaphore){
        Printer.getInstance().print("adding semapore: " + semaphore.getID(), "green");
        giveOrderingToSemaphore(semaphore);
        crossroad.getSemaphores().add(semaphore);
        sendCurrentState();
        monitorer.setNumberOfSemaphores(crossroad.getSemaphores().size());
        try {
            MongoDataStore.getInstance().addSemaphoreToMongo(crossroad.getID(), semaphore);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        printSemaphores();
    }

    public void removeSemaphore(Semaphore semaphore){
        for(Semaphore s : crossroad.getSemaphores()){
            if(s.getID().equals(semaphore.getID())){
                System.out.println("removing semaphore " + s.getID());
                crossroad.getSemaphores().remove(s);
                monitorer.setNumberOfSemaphores(crossroad.getSemaphores().size());
                break;
            }
        }
        printSemaphores();
    }

    private void printSemaphores(){
        if(crossroad.getSemaphores().size() != 0) {
            Printer.getInstance().print("semaphores binded: ", "green");
            for (Semaphore s : crossroad.getSemaphores())
                Printer.getInstance().print(s.getID(), "green");
        }
    }

    private void sendCurrentState(){
        Message m = new Message(crossroad.getID(), 10);
        m.setListOfSemaphores(crossroad.getSemaphores());
        m.setCurrentCycle(monitorer.getCurrentCycle());
        for(Semaphore s: crossroad.getSemaphores()) {
            fp.sendMessage("address", m, s.getID());
            Printer.getInstance().print("current state sent to " + s.getID(), "yellow");
        }
    }

    public void sendGreen(Semaphore greenSemaphore) {

    }

    public void addChatID(long chat_id) {
        this.chatids.add(chat_id);
    }

    private void giveOrderingToSemaphore(Semaphore s){
        if(crossroad.getSemaphores().size() == 0)
            s.setOrder(0);
        else
            s.setOrder(crossroad.getSemaphores().get(crossroad.getSemaphores().size()-1).getOrder() + 1);
        System.out.println("imposto ordinamento a " + s.getOrder());
    }

    public void tellMonitorerToSendInfos(String ip, String id){
        Printer.getInstance().print("dico al monitorer di mandare le info: ip = " + ip + ", id = " + id, "yellow");
        monitorer.setCrossroad(crossroad);
        monitorer.sendCrossroadSituation(ip, id);
    }

    public void sendMalfunctionToSemaphores(Message mex){

        telegramBot.sendMessage("🚦" + "malfunction at semaphore: " +mex.getID() + "\n" +
                mex.getBrokenBulbs(), "@SDCChannel");
        thereIsaMalfunction = true;
        Printer.getInstance().print("\n\n\n è arrivato un malfunzionamento su "+ mex.getID() + "\n\n\n", "yellow");
        try {
            //send malfunction message to semaphores
            Message m = new Message(crossroad.getID(), 404);
            for(Semaphore s: crossroad.getSemaphores()) {
                fp.sendMessage("address", m, s.getID());
                Printer.getInstance().print("current state sent to " + s.getID(), "yellow");
            }
            MongoDataStore.getInstance().addMalfunctionToDB(mex.getID());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private class TimerClass extends TimerTask {
        @Override
        public void run() {
            sendCurrentState();
            printSemaphores();
            while(crossroad.getSemaphores().size() == 0){
                try {
                    System.out.println("non invio in quanto zero");
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!thereIsaMalfunction)
                twopc.votingPhase(crossroad.getSemaphores(), crossroad.getSemaphores().get(0).getID(), crossroad.getID());
        }
    }
}


