package rabbit.test;

import rabbit.controllers.Send;
import rabbit.entities.Message;
import rabbit.entities.Semaphore;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Author : Simone D'Aniello
 * Date :  13-Feb-18.
 */
public class Simulation {
    public static void main(String[] args) throws Exception {
        noFailures(3);
    }




    private static void noFailures(Integer num) throws IOException, TimeoutException, InterruptedException {

        Semaphore s = new Semaphore(101);
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
}
