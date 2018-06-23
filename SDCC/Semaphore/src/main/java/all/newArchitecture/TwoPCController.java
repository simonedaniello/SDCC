package all.newArchitecture;

import all.front.FirstProducer;
import entities.Message;
import entities.Semaphore;
import entities.system.Printer;

import java.util.ArrayList;
import java.util.Random;

public class TwoPCController {

    private Boolean isMe = false;
    private SemaphoreClass sc;
    private FirstProducer fp;
    private String s;
    private String currentPhase = "idle"; //there is no active 2pc phase


    public TwoPCController(SemaphoreClass sc, FirstProducer fp){
        setAttributes(sc, fp);
    }

    public void setAttributes(SemaphoreClass sc, FirstProducer fp){
        this.sc = sc;
        this.fp = fp;
    }

    public void votingPhase(String s, String crossroad, Boolean isMe, ArrayList<Semaphore> sems){
        currentPhase = "voting";
        this.s = s;
        this.isMe = isMe;
        Message m = new Message(s, 311);
        fp.sendMessage("address", m, crossroad);
        gossipAll(sems, (float) 25.0);
    }

    public void commitPhase(String crossroad){
        currentPhase = "commit";
        Message m = new Message(s, 312);
        fp.sendMessage("address", m, crossroad);
        if(isMe) {
            sc.changeSemaphoreLight(1);
            Printer.getInstance().print("I BECOME GREEN", "blue");
        }
        else {
            sc.changeSemaphoreLight(0);
            Printer.getInstance().print("I BECOME RED", "green");
        }
    }

    public void rollback(String crossroad){
        currentPhase = "rollback";
        sc.emergencyMode();
        Message m = new Message(s, 312);
        fp.sendMessage("address", m, crossroad);
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void gossipAll (ArrayList<Semaphore> sems, float prob){
        Random rand = new Random();
        for (Semaphore s : sems) {
            if (rand.nextInt(100) < prob ) {
                Printer.getInstance().print("invio messaggio di gossiping", "cyan");
                currentPhase = "commit";
                Message m = new Message(s.getID(), 301);
                fp.sendMessage("address", m, s.getID());
            }
        }

    }
}


