package main.java.rabbit.test;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author : Simone D'Aniello
 * Date :  20-Feb-18.
 */
public class CrossroadTests {

    @Test
    public void send_message_crossroad(){
        Simulation s = new Simulation();
        int result = s.sendMessage();
        assertEquals(1, result);
    }

    @Test
    public void add_remove_toCrossroad(){
        Simulation s = new Simulation();
        int result = s.crossRoadSimulation();
        assertEquals(1, result);
    }
}
