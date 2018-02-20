package rabbit.controllers;

import rabbit.entities.Message;
import rabbit.entities.Semaphore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Author : Simone D'Aniello
 * Date :  19-Feb-18.
 */
public class SemaphoreController {

    Semaphore semaphore;

    public SemaphoreController(Integer ID){
        this.semaphore = new Semaphore(ID);
    }

    private void start(){
        try {
            semaphore.getReceive().receiveMessage("localhost", semaphore.getCrossroads());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getState() {
        System.out.println("\n------\nID: " + semaphore.getID() + "\nState: " + semaphore.getState()+ "\nBindings: ");
        for(String c : semaphore.getCrossroads()){
            System.out.println("\t'" + c + "'");
        }
        System.out.println("------\n");

    }

    public void cangeState(Integer state) {
        semaphore.setState(state);
        //do things
    }


    public void addCrossroad(String crossroad) {
        System.out.println("added crossroad : " + crossroad);
        ArrayList<String> temp;
        temp = semaphore.getCrossroads();
        temp.add(crossroad);
        semaphore.setCrossroads(temp);

        semaphore.getReceive().addBindings(crossroad);
    }

    public void removeCrossroad(String crossroad) {
        System.out.println("added crossroad : " + crossroad);
        ArrayList<String> temp;
        temp = semaphore.getCrossroads();
        temp.remove(crossroad);
        semaphore.setCrossroads(temp);

        semaphore.getReceive().removeBinding(crossroad);
    }

    public void sendRequest(String queue, String topic) {
        Message m = new Message(semaphore.getID());
        Send s1 = new Send(semaphore.getID());
        try {
            s1.sendMessage("localhost", m, queue, topic);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public Integer getID() {
        return semaphore.getID();
    }

}
