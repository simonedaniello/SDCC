package main.java.test;

import main.java.Crossroad;
import main.java.controller.SemaphoreController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class GeneralTester {

    public static void main(String[] args) throws InterruptedException {
        generalSelf();
        //fromProperties();
    }

    private static void generalSelf() throws InterruptedException  {
        SemaphoreController semaphoreController1 = new SemaphoreController("sem1", "via sem1");
        SemaphoreController semaphoreController2 = new SemaphoreController("sem2", "via sem2");
        SemaphoreController semaphoreController3 = new SemaphoreController("sem3", "via sem2");

        Crossroad c = new Crossroad("cross1", "via nulla");
        semaphoreController1.addToCrossroad(c);
        //TimeUnit.SECONDS.sleep(1);
        semaphoreController3.addToCrossroad(c);
        //TimeUnit.SECONDS.sleep(1);
        semaphoreController2.addToCrossroad(c);
        //TimeUnit.SECONDS.sleep(1);
        semaphoreController1.removeCrossroad(c);

        TimeUnit.SECONDS.sleep(2);
        semaphoreController1.printState();
        semaphoreController2.printState();
        semaphoreController3.printState();



        //System.exit(0);
    }

    private static void fromProperties(){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            /*  ID = semaphore1
                greenTogether = semaphore2
                street = via dei borboni
                crossroad = crossroad1
             */
            System.out.println("ID: " + prop.getProperty("ID"));
            System.out.println("greenTogether: " + prop.getProperty("greenTogether"));
            System.out.println("street: " + prop.getProperty("street"));
            System.out.println("crossroad: " + prop.getProperty("crossroad"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
