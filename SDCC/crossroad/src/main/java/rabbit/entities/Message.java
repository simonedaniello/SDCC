package main.java.rabbit.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  13-Feb-18.
 */
public class Message implements Serializable {

    public Message(Integer ID) {
        this.ID = ID;
    }

    private int ID;
    private ArrayList<Integer> vectorClock;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<Integer> getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(ArrayList<Integer> vectorClock) {
        this.vectorClock = vectorClock;
    }

}
