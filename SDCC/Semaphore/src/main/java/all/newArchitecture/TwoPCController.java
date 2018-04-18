package all.newArchitecture;

import all.front.FirstProducer;
import main.java.Message;
import main.java.system.Printer;

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

    public void votingPhase(String s, String crossroad, Boolean isMe){
        currentPhase = "voting";
        this.s = s;
        this.isMe = isMe;
        Message m = new Message(s, 311);
        fp.sendMessage("address", m, crossroad);
    }

    public void commitPhase(String crossroad){
        currentPhase = "commit";
        Message m = new Message(s, 312);
        fp.sendMessage("address", m, crossroad);
        if(isMe)
            Printer.getInstance().print("I BECOME GREEN", "blue");
        else
            Printer.getInstance().print("I BECOME RED", "green");
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
}
