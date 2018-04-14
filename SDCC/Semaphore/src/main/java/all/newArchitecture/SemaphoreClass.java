package all.newArchitecture;

import all.SemaphoreSensorDataProducer;
import all.front.FirstConsumer;
import all.front.FirstProducer;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.util.ArrayList;

public class SemaphoreClass implements Runnable{

    private Semaphore semaphore;
    private FirstConsumer fc;
    private FirstProducer fp;
    private TwoPCController twopc;

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
        startListeningKafka();
        requestOtherSemaphores();
//        sendSensorData();
    }

    /**
     * TODO
     * send the list of cars detected through kafka to flinkDispatcher
     */
    public void sendSensorData(){
        new SemaphoreSensorDataProducer(this.fp);
    }

    /**
     * TODO
     * send a malfunction warning to flinkDispatcher using kafka
     */
    public void sendMalfunction(){

    }

    /**
     * TODO
     * start 2PC for decide green light
     * @param youAreGreen
     */
    public void start2pc(boolean youAreGreen){
        twopc.votingPhase(semaphore.getID(), semaphore.getCrossroad(), youAreGreen);
        fc.setCrossroad(semaphore.getCrossroad());
    }

    /**
     * TODO
     * enter in emegency mode
     */
    public void emergencyMode(){
        Printer.getInstance().print("entro in emergency mode", "red");
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
        this.semaphore.setSemaphores(listOfSemaphores);
        Handyman.getInstance().printStatus(this.semaphore);
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

    @Override
    public void run() {
        fc.runConsumer(this.semaphore.getID(), this.semaphore.getCrossroad());
    }

}
