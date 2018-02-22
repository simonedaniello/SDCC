package main.java;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class Message implements Serializable {

    private String ID;
    private ArrayList<Integer> vectorClock;
    private ArrayList<Semaphore> listOfSemaphores;

    /*
    * code values:
    *   1: semaphore added to crossroad
    *   -1: removing semaphore from crossroad
    *   */
    private int code;
    private String semaphoreCode;
    private String semaphoreAddress;

    public Message(String ID, int code){
            setID(ID);
            setCode(code);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<Integer> getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(ArrayList<Integer> vectorClock) {
        this.vectorClock = vectorClock;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSemaphoreCode() {
        return semaphoreCode;
    }

    public void setSemaphoreCode(String semaphoreCode) {
        this.semaphoreCode = semaphoreCode;
    }

    public String getSemaphoreAddress() {
        return semaphoreAddress;
    }

    public void setSemaphoreAddress(String semaphoreAddress) {
        this.semaphoreAddress = semaphoreAddress;
    }

    public ArrayList<Semaphore> getListOfSemaphores() {
        return listOfSemaphores;
    }

    public void setListOfSemaphores(ArrayList<Semaphore> listOfSemaphores) {
        this.listOfSemaphores = listOfSemaphores;
    }
}
