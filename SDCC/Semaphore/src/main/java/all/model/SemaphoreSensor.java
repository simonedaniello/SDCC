package all.model;

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
    private int carsInTimeUnit;
    private double meanSpeed;
    private boolean isGreenWorking;
    private boolean isYellowWorking;
    private boolean isRedWorking;


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

    public int getCarsInTimeUnit() {
        return carsInTimeUnit;
    }

    public void setCarsInTimeUnit(int carsInTimeUnit) {
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


    public void initializeSensor(){


        setPosition();
        //semaphoreID =new String(messageDigest.digest());
        semaphoreID = org.apache.commons.codec.digest.DigestUtils.sha256Hex(latitude+longitude);
//        System.out.println(semaphoreID);
        crossroadID = UUID.randomUUID().toString();
        carsInTimeUnit = 0;
        meanSpeed = 0;
        timestamp = (new Date()).toString();
        greenDuration = 60;
        isGreenWorking = true;
        isRedWorking = true;
        isYellowWorking = true;




    }

    private void setPosition() {

        Random rand = new Random();

        int latPrefix = 33;
        int longPrefix = -96;
        latitude = String.valueOf((float)latPrefix + rand.nextFloat());
        longitude = String.valueOf((float)longPrefix + rand.nextFloat());

    }


}