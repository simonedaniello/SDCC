package all.newArchitecture;

import all.front.FirstProducer;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

public class TwoPCController {

    private String undolog;
    private String redolog;
    private Boolean isMe = false;
    private SemaphoreClass sc;
    private FirstProducer fp;
    private String s;


    public TwoPCController(SemaphoreClass sc, FirstProducer fp){
        setAttributes(sc, fp);
    }

    public void setAttributes(SemaphoreClass sc, FirstProducer fp){
        this.sc = sc;
        this.fp = fp;
    }


    public void votingPhase(String s, String crossroad, Boolean isMe){
        this.s = s;
        this.isMe = isMe;
        Message m = new Message(s, 311);
        fp.sendMessage("address", m, crossroad);
    }

    public void commitPhase(String crossroad){
        Message m = new Message(s, 312);
        fp.sendMessage("address", m, crossroad);
        if(isMe)
            Printer.getInstance().print("I BECOME GREEN", "blue");
        else
            Printer.getInstance().print("I BECOME RED", "green");
    }

    public void rollback(String crossroad){
        sc.emergencyMode();
        Message m = new Message(s, 312);
        fp.sendMessage("address", m, crossroad);
    }


}
