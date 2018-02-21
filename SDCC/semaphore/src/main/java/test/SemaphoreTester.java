package main.java.test;

import main.java.controller.SemaphoreController;
import org.junit.Test;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class SemaphoreTester {

    @Test
    public void addSemaphores(){
        SemaphoreController sc1 = new SemaphoreController("0", "via 1");
        SemaphoreController sc2 = new SemaphoreController("1", "via 1");
    }
}
