package all.control;

import all.front.FirstConsumer;
import all.front.FirstProducer;
import main.java.Crossroad;
import main.java.Semaphore;
import main.java.system.Printer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 */
public class MonitorController implements Runnable{

    private static MonitorController instance = new MonitorController();

    private ArrayList<Crossroad> crossroads;
    private QuerySolver qs;

    private MonitorController(){
        qs = new QuerySolver();
        crossroads = new ArrayList<>();
        String ID = "monitor";
        FirstConsumer.getInstance().setAttributes(this, qs);
        FirstConsumer.getInstance().subscribeToTopic(ID);
        (new Thread(this)).start();
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

    public Boolean flinkAddCrossroad(Crossroad crossroad){
        try {
            qs.addCrossroad(crossroad);
            return true;
        } catch(Exception e){
            Printer.getInstance().print(e.getMessage(), "red");
            return false;
        }
    }

    public Boolean flinkAddSemaphore(Semaphore semaphore){
        try {
            qs.addSemaphore(semaphore);
            return true;
        } catch(Exception e){
            Printer.getInstance().print(e.getMessage(), "red");
            return false;
        }
    }

    public String flinkGetGeneralSituation(){
        try {
            return qs.getGeneralSituation();
        } catch(Exception e) {
            return "problem";
        }

    }

    public String flinkGetCrossroadSituation(Crossroad crossroad){
        try {
            return qs.getCrossroadSituation(crossroad);
        } catch(Exception e) {
            return "problem";
        }
    }

    public String flinkGetQueries(){
        try {
            return qs.getQueries();
        } catch(Exception e) {
            return "problem";
        }
    }

    @Override
    public void run() {
        FirstConsumer.getInstance().runConsumer();
    }
}
