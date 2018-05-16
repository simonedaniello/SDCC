package all.newArchitecture;

import all.SemaphoreSensorDataProducer;
import all.front.FirstConsumer;
import all.front.FirstProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SemaphoreClass implements Runnable{

    private main.java.Semaphore semaphore;
    private FirstConsumer fc;
    private FirstProducer fp;
    private TwoPCController twopc;
    private Boolean emergencyModeOn = false;
    private Boolean isGossiping = false;
    private int emergencymodetimeseconds;

    public SemaphoreClass() {}

    /**
     * when this class is created it adds a new semaphore in the list.
     * The attributes of each semaphore are:
     *      String id
     *      String latitude
     *      String longitude
     *      ArrayList(Semaphore) semaphores
     *      Boolean lightBulbeStatus
     *      String street
     *      String crossroad
     *      ArrayList(Car) carsDetected
     * @param s
     */
    public void registerSemaphore(Semaphore s){
        this.semaphore = s;
        this.semaphore.setID(idGenerator(this.semaphore.getLatitude(), this.semaphore.getLongitude()));
        fc = new FirstConsumer();
        fp = new FirstProducer();
        twopc = new TwoPCController(this, fp);

        fc.setAttributes(this, twopc, s.getCrossroad());

        //getting emergency mode time cycle
        Properties properties = new Properties();
        String filename = "semaphoresConfig.props";
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
        emergencymodetimeseconds = Integer.parseInt(properties.getProperty("periodtimeforsinglesemaphore"));

        startListeningKafka();
        requestOtherSemaphores();
        sendSensorData();
    }

    /**
     * TODO
     * send the list of cars detected through kafka to flinkDispatcher
     */
    private void sendSensorData(){
        try {
            new SemaphoreSensorDataProducer(this.fp, semaphore);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
     * start 2PC for decide green light
     * @param youAreGreen
     * @param id
     */
    public void start2pc(boolean youAreGreen, String id){
        if(!isGossiping || id.equals(semaphore.getCrossroad())) {
            emergencyModeOn = false;
            twopc.votingPhase(semaphore.getID(), semaphore.getCrossroad(), youAreGreen, semaphore.getSemaphores());
            fc.setCrossroad(semaphore.getCrossroad());
            isGossiping = true;
        }
    }

    /**
     * TODO
     * enter in emegency mode
     */
    public void emergencyMode(){
        emergencyModeOn = true;

        while(emergencyModeOn){

            int start = semaphore.getOrder() * emergencymodetimeseconds;
            int end = start + emergencymodetimeseconds;
            System.out.println("start = " + start + ", end = " + end);
            long currentTimeCycle = (Calendar.getInstance().getTimeInMillis()/1000)%(emergencymodetimeseconds*semaphore.getSemaphores().size());
            if(currentTimeCycle < end && currentTimeCycle > start){
                semaphore.setLight(1);
                Printer.getInstance().print("I BECOME GREEN: " + semaphore.getID(), "green");
            }
            else {
                semaphore.setLight(0);
                Printer.getInstance().print("I BECOME RED: " + semaphore.getID(), "red");
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO
     * run the kafka consumer
     */
    private void startListeningKafka(){
        (new Thread(this)).start();
    }

    /**
     * function used to update the semaphore list due to a change in the crossroad
     * @param listOfSemaphores
     */
    public void updateSemaphoreList(ArrayList<Semaphore> listOfSemaphores){
//        Printer.getInstance().print("\n\n\n updating semaphore list \n\n\n", "yellow");
        this.semaphore.setSemaphores(listOfSemaphores);
        Handyman.getInstance().printStatus(this.semaphore);
        for(Semaphore s: listOfSemaphores){
            if (s.getID().equals(semaphore.getID())) {
                Printer.getInstance().print(semaphore.getID() + ": setto come ordinamento --- " + s.getOrder(), "yellow");
                semaphore.setOrder(s.getOrder());
            }
        }
    }

    /**
     * TODO
     * function that generates the id given the latitude and the longitude of the semaphore
     * @param latitude
     * @param longitude
     * @return
     */
    private String idGenerator(String latitude, String longitude){
        String id = org.apache.commons.codec.digest.DigestUtils.sha256Hex(latitude + longitude);
        Printer.getInstance().print("{latitude: " + latitude + ", longitude: " + longitude + ", id: " + id + " }", "green");
        return id;
    }

    /**
     * TODO
     * when a semaphore is created it asks the controller through kafka the id of
     * the other semaphores in the crossroad
     */
    private void requestOtherSemaphores(){
        Message m = new Message();
        m.setCode(1);
        m.setSemaphore(this.semaphore);
        Printer.getInstance().print("sending registration with id: " + this.semaphore.getID(), "blue");
        fp.sendMessage(semaphore.getControllerIP(), m, semaphore.getCrossroad());
    }

    public void crossroadMalfunction() {
        emergencyModeOn = false;
        Printer.getInstance().print("entering in loop yellow mode (due to malfunction)", "yellow");
        semaphore.setLight(2);
    }

    public void changeSemaphoreLight(int i) {
        emergencyModeOn = false;
        semaphore.setLight(i);
    }

    @Override
    public void run() {
        fc.runConsumer(this.semaphore.getID(), this.semaphore.getCrossroad());
    }
}
