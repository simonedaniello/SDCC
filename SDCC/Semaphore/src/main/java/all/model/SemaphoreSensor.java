package all.model;

import main.java.Semaphore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class SemaphoreSensor {

    private String crossroadID;
    private String semaphoreID;
    private String latitude;
    private String longitude;
    private String timestamp;
    private int greenDuration;
    private double carsInTimeUnit;
    private double meanSpeed;
    private boolean isGreenWorking = true;
    private boolean isYellowWorking = true;
    private boolean isRedWorking = true;


    public String getCrossroadID() {
        return crossroadID;
    }

    public void setCrossroadID(String crossroadID) {
        this.crossroadID = crossroadID;
    }

    public String getSemaphoreID() {
        return semaphoreID;
    }

    public void setSemaphoreID(String semaphoreID) {
        this.semaphoreID = semaphoreID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getGreenDuration() {
        return greenDuration;
    }

    public void setGreenDuration(int greenDuration) {
        this.greenDuration = greenDuration;
    }

    public double getCarsInTimeUnit() {
        return carsInTimeUnit;
    }

    public void setCarsInTimeUnit(double carsInTimeUnit) {
        this.carsInTimeUnit = carsInTimeUnit;
    }

    public double getMeanSpeed() {
        return meanSpeed;
    }

    public void setMeanSpeed(double meanSpeed) {
        this.meanSpeed = meanSpeed;
    }

    public boolean isGreenWorking() {
        return isGreenWorking;
    }

    public void setGreenWorking(boolean greenWorking) {
        isGreenWorking = greenWorking;
    }

    public boolean isYellowWorking() {
        return isYellowWorking;
    }

    public void setYellowWorking(boolean yellowWorking) {
        isYellowWorking = yellowWorking;
    }

    public boolean isRedWorking() {
        return isRedWorking;
    }

    public void setRedWorking(boolean redWorking) {
        isRedWorking = redWorking;
    }


    public void initializeSensor(Semaphore sem, long ratefrattomille){

        setPosition(sem);
        semaphoreID = sem.getID();
        //semaphoreID =new String(messageDigest.digest());
        //semaphoreID = org.apache.commons.codec.digest.DigestUtils.sha256Hex(latitude+longitude);
//        System.out.println(semaphoreID);
        crossroadID = sem.getCrossroad();
        carsInTimeUnit = 0;
        meanSpeed = (double) (new Random().nextInt(80) + 20);
        timestamp = (new Date()).toString();
        greenDuration = 60;

        //Randomize broken bulb
        Random rand = new Random();
        double g = rand.nextInt(10000);
        double y = rand.nextInt(10000);
        double r = rand.nextInt(10000);

        if (g>=ratefrattomille)
            isGreenWorking = false;
        if (y>=ratefrattomille)
            isRedWorking = false;
        if (r>=ratefrattomille)
            isYellowWorking = false;




    }

    private void setPosition(Semaphore sem) {

        latitude = sem.getLatitude();
        longitude = sem.getLongitude();
    }


}