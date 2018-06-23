package all.newArchitecture;

import entities.Semaphore;
import entities.system.Printer;

public class Handyman {

    private static Handyman instance = new Handyman();

    private Handyman(){}

    public static Handyman getInstance() {
        return instance;
    }

    void printStatus(Semaphore s){
        Printer.getInstance().print("ID: " + s.getID() + "; Crossroad: " + s.getCrossroad() + "; Controller: " + s.getControllerIP() + "list other semaphores: " + s.getSemaphores() + ";", "green");
    }
}
