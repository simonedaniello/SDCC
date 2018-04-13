package all.newArchitecture;

import all.front.FirstConsumer;
import all.front.FirstProducer;
import main.java.Message;
import main.java.Semaphore;

import java.util.ArrayList;

public class SemaphoreClass implements Runnable{

    private static SemaphoreClass instance = new SemaphoreClass();
    private Semaphore semaphore;

    private SemaphoreClass() {}

    public static SemaphoreClass getInstance(){
        return instance;
    }

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
        (new Thread(this)).start();
        requestOtherSemaphores();
    }

    /**
     * TODO
     * send the list of cars detected through kafka to flinkDispatcher
     */
    public void sendSensorData(){

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

    }

    /**
     * TODO
     * enter in emegency mode
     */
    public void emergencyMode(){

    }

    /**
     * TODO
     * run the kafka consumer
     */
    private void startListeningKafka(){

    }

    /**
     * function used to update the semaphore list due to a change in the crossroad
     * @param listOfSemaphores
     */
    public void updateSemaphoreList(ArrayList<Semaphore> listOfSemaphores){
        this.semaphore.setSemaphores(listOfSemaphores);
    }

    /**
     * TODO
     * function that generates the id given the latitude and the longitude of the semaphore
     * @param latitude
     * @param longitude
     * @return
     */
    private String idGenerator(String latitude, String longitude){
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(latitude + longitude);
    }

    /**
     * TODO
     * when a semaphore is created it asks the controller through kafka the id of
     * the other semaphores in the crossroad
     */
    private void requestOtherSemaphores(){
        Message m = new Message();
        m.setCode(3);
        m.setSemaphore(this.semaphore);
        FirstProducer.getInstance().sendMessage(semaphore.getControllerIP(), m, "fromSemaphoreToControl");
    }

    @Override
    public void run() {
        FirstConsumer.getInstance().runConsumer(this.semaphore.getID());
    }

}
