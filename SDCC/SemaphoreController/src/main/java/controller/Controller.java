package controller;


/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 *
 * Format Message used:
 *      Class name: MessageMonitor
 *      Fields:
 *          int Code
 *          Map<String, Boolean, float> semaphoreToCheck        //decide
 *
 */




public class Controller {

    public Controller(){

    }

    /**
     * Connection to Kafka channel
     */
    public void retrieveDataFromKafka(){

    }

    /**
     * Retrieving the decision message that has to be forwarded to the corresponding semaphores
     */
    public void sendGreenSemaphore(){

    }

    /**
     * Retrieving the message that a semaphore has a malfunction
     */
    public void sendSomethingWentWrongMessage(){

    }

}
