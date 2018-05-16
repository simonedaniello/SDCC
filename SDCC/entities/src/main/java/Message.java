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
    private Crossroad crossroad;
    private int currentCycle;
    private Semaphore semaphore;
    private boolean youAreGreen;
    private String IP;
    private FlinkResult flinkResult;
    private ArrayList<FlinkResult> partialRanking;
    private String semaphoreTuple;
    private String brokenBulbs;
    /*
    *
    * code values:
    *   1: semaphore added to crossroad
    *   -1: removing semaphore from crossroad
    *   400 : monitor message
    *   401 : request of the crossroad monitor
    *   10 : get list of semaphores from crossroad
    *   500 : send status to monitorBackEnd
    *
    */
    private int code;


    public Message(String ID, int code){
            setID(ID);
            setCode(code);
    }

    public Message(){}

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

    public ArrayList<Semaphore> getListOfSemaphores() {
        return listOfSemaphores;
    }

    public void setListOfSemaphores(ArrayList<Semaphore> listOfSemaphores) {
        this.listOfSemaphores = listOfSemaphores;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void setCurrentCycle(int currentCycle) {
        this.currentCycle = currentCycle;
    }

    public Crossroad getCrossroad() {
        return crossroad;
    }

    public void setCrossroad(Crossroad crossroad) {
        this.crossroad = crossroad;
    }

    public boolean isYouAreGreen() {
        return youAreGreen;
    }

    public void setYouAreGreen(boolean youAreGreen) {
        this.youAreGreen = youAreGreen;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public FlinkResult getFlinkResult() {
        return flinkResult;
    }

    public void setFlinkResult(FlinkResult flinkResult) {
        this.flinkResult = flinkResult;
    }

    public ArrayList<FlinkResult> getPartialRanking() {
        return partialRanking;
    }

    public void setPartialRanking(ArrayList<FlinkResult> partialRanking) {
        this.partialRanking = partialRanking;
    }

    public String getSemaphoreTuple() {
        return semaphoreTuple;
    }

    public void setSemaphoreTuple(String semaphoreTuple) {
        this.semaphoreTuple = semaphoreTuple;
    }

    public String getBrokenBulbs() {
        return brokenBulbs;
    }

    public void setBrokenBulbs(String brokenBulbs) {
        this.brokenBulbs = brokenBulbs;
    }
}
