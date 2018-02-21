package main.java.test;

import main.java.controller.SemaphoreController;
import main.java.entities.Crossroad;

import java.util.concurrent.TimeUnit;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class GeneralTester {
    public static void main(String[] args) throws InterruptedException {
        SemaphoreController semaphoreController1 = new SemaphoreController("sem1", "via sem1");
        SemaphoreController semaphoreController2 = new SemaphoreController("sem2", "via sem2");
        SemaphoreController semaphoreController3 = new SemaphoreController("sem3", "via sem2");

        Crossroad c = new Crossroad("cross1", "via nulla");
        semaphoreController1.addToCrossroad(c);
        TimeUnit.SECONDS.sleep(1);
        semaphoreController3.addToCrossroad(c);
        TimeUnit.SECONDS.sleep(1);
        semaphoreController2.addToCrossroad(c);
        TimeUnit.SECONDS.sleep(1);
        semaphoreController1.removeCrossroad(c);

        semaphoreController1.printState();
        semaphoreController2.printState();
        semaphoreController3.printState();


        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }
}
