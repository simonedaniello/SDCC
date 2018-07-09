package all.model;

import entities.Semaphore;

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
    private String brokenbulbs = "Broken Bulbs: ";


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

    public String getBrokenbulbs() {
        return brokenbulbs;
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
        rand.setSeed(System.currentTimeMillis());
        double g = rand.nextInt(1000);
        double y = rand.nextInt(1000);
        double r = rand.nextInt(1000);

        if (g>=ratefrattomille){
            System.out.println("lampadina rotta");
            isGreenWorking = false;
            brokenbulbs = brokenbulbs + "Green, ";}

        if (y>=ratefrattomille){
            System.out.println("lampadina rotta");
            isYellowWorking = false;
            brokenbulbs = brokenbulbs + "Yellow, ";}

        if (r>=ratefrattomille){
            System.out.println("lampadina rotta");
            isRedWorking = false;
            brokenbulbs = brokenbulbs + "Red, ";}

    }

    private void setPosition(Semaphore sem) {

        latitude = sem.getLatitude();
        longitude = sem.getLongitude();
    }


}