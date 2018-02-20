package main.java.rabbit.entities;

import main.java.rabbit.controllers.Receive;

import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  15-Feb-18.
 */
public class Semaphore{

    private Integer ID;
    private Integer state; // 0: free, 1: requested, 2: in CS
    private ArrayList<String> crossroads;
    private Message m;
    private Receive r;

    public Semaphore(Integer ID){
        setID(ID);
        setState(0);
        ArrayList<String > c= new ArrayList<String>();
        setCrossroads(c);
        Message message  = new Message(ID);
        setMessage(message);
        Receive receive = new Receive(this.ID, "traffic");
        setReceive(receive);
        try {
            r.receiveMessage("localhost", this.crossroads);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setID(Integer ID){ this.ID = ID; }
    public void setState(Integer state) { this.state = state; }
    public void setCrossroads(ArrayList<String> crossroads){ this.crossroads = crossroads; }
    public void setMessage (Message m) {this.m = m; }
    public void setReceive (Receive r){ this.r = r; }

    public Integer getID(){ return this.ID; }
    public Integer getState() {return this.state; }
    public ArrayList<String> getCrossroads() {return this.crossroads; }
    public Message getMessage() { return this.m; }
    public Receive getReceive() {return this.r; }



}
