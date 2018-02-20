package main.java.rabbit.test;



import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import main.java.rabbit.controllers.CrossroadController;
import main.java.rabbit.controllers.SemaphoreController;
import main.java.rabbit.controllers.Send;
import main.java.rabbit.entities.Message;

/**
 * Author : Simone D'Aniello
 * Date :  13-Feb-18.
 */
public class Simulation {

    public static void main(String[] args) {
        try {
            noFailures();
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void noFailures() throws IOException, TimeoutException, InterruptedException {

        SemaphoreController s = new SemaphoreController(101);
        s.addCrossroad("incrocio 1");
        s.getState();

        Message m = new Message(10);
        Send s1 = new Send(15);
        s1.sendMessage("localhost", m, "traffic", "incrocio 1");
        s1.sendMessage("localhost", m, "traffic", "incrocio 4");
        s1.sendMessage("localhost", m, "traffic", "incrocio 2");
        s1.sendMessage("localhost", m, "traffic", "incrocio 3");


        s.addCrossroad("incrocio 2");
        s.getState();

        TimeUnit.SECONDS.sleep(2);
        s1.sendMessage("localhost", m, "traffic", "incrocio 2");

        s.removeCrossroad("incrocio 2");
        s.getState();

        TimeUnit.SECONDS.sleep(2);
        s1.sendMessage("localhost", m, "traffic", "incrocio 2");
        s1.sendMessage("localhost", m, "traffic", "incrocio 1");

        TimeUnit.SECONDS.sleep(2);
        System.exit(0);
    }

    Integer crossRoadSimulation(){

        CrossroadController c = new CrossroadController(1234567, "via lombardo dei lombardi");
        SemaphoreController s = new SemaphoreController(0);
        SemaphoreController s2 = new SemaphoreController(1);
        c.addSemaphore(s);
        s2.addToCrossRoad(c);
        c.printState();
        c.removeSemaphore(s2.getID());
        c.printState();
        return 1;
    }

    Integer sendMessage() {
        CrossroadController c = new CrossroadController(54321, "via dei bicefali");
        SemaphoreController s1 = new SemaphoreController(0);
        SemaphoreController s2 = new SemaphoreController(1);
        s1.addCrossroad(String.valueOf(c.getID()));
        s2.addToCrossRoad(c);
        s1.getState();
        s2.getState();
        c.sendMessage(342);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
