package all.control;

import main.java.Crossroad;
import main.java.Semaphore;
import main.java.system.Printer;
import all.front.Receiver;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 */
public class MonitorController {

    private static MonitorController instance = new MonitorController();

    private ArrayList<Crossroad> crossroads;
    private String ID;

    private MonitorController(){
        crossroads = new ArrayList<>();
        ID = "monitor";
        Receiver r = new Receiver(ID, "traffic", this);
        try {
            r.receiveMessage("localhost", ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printState(){
        Printer.getInstance().print("current state: ", "cyan");
        for(Crossroad c: crossroads){
            Printer.getInstance().print("\t"+ c.getID(), "blue");
            for(Semaphore s : c.getSemaphores()){
                Printer.getInstance().print("\t\t"+ s.getID(), "yellow");
            }
        }
    }

    public static MonitorController getInstance(){
        return instance;
    }

    public ArrayList<Crossroad> getCrossroads() {
        return crossroads;
    }

    public void setCrossroads(ArrayList<Crossroad> crossroads) {
        this.crossroads = crossroads;
    }

    public void addCrossroadToList(Crossroad cross){
        int k = 0;
        for (Iterator<Crossroad> iter = crossroads.listIterator(); iter.hasNext(); ) {
            Crossroad c = iter.next();
            Printer.getInstance().print("comparo " + c.getID() +" e " + cross.getID(), "red");
            if(c.getID().equals(cross.getID())){
                k = 1;
            }
            if (k == 1) {
                iter.remove();
                Printer.getInstance().print("ho rimosso "+ c.getID(), "red");
                k = 0;
            }
        }
        crossroads.add(cross);
        printState();
    }
}
