package all.controllers;



// TODO MONITORER CLASS
// TODO dato che il semaforo non fa nulla per la maggior parte del tempo, implementiamo sul semaforo una chiamata sema REST
// TODO e facciamo comunicare il front direttamente con il semaforo in questione per sapere la situazione sul traffico


/**
 * Author : Simone D'Aniello
 * Date :  23-Feb-18.
 *
 * Format Message used:
 *      Class name: MessageMonitor
 *      Fields:
 *          int Code
 *          ArrayList<String> tenIntersectionFifteen            //query1
 *          ArrayList<String> tenIntersectionOneHour            //query1
 *          ArrayList<String> tenIntersectionOneDay             //query1
 *          ArrayList<String> medianTooHighFifteen              //query2
 *          ArrayList<String> medianTooHighOneHour              //query2
 *          ArrayList<String> medianTooHighOneDay               //query2
 *          ArrayList<String> tooMuchCongestion                 //query3
 *          ArrayList<Semaphore> semaphoresIdWithMalfunctions   //monitoring
 *          float throughput                                    //monitoring
 *          float latency                                       //monitoring
 *
 *
 */
public class Monitorer {

    public Monitorer(){

    }

    /**
     * Retrieving data from kafka channel.
     * With this function the monitor listens on every topic and call the correct function
     */
    public void retrieveDataFromKafka(){

    }

    /**
     * Work with data from query1
     */
    public void receivedQuery1(){

    }

    /**
     * Work with data from query2
     */
    public void receivedQuery2(){

    }

    /**
     * Work with data from query3
     */
    public void receivedQuery3(){

    }

    /**
     * Signal a semaphore malfunction
     */
    public void receivedSemaphoresMalfunction(){

    }

    /**
     * Saving data to MongoDB,
     * Is not important the function the data come from.
     */
    public void saveDataOnMongo(){

    }












//    private Monitorer() {
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerClass(), 10000, 15000); // every 15 seconds
//    }


//    private class TimerClass extends TimerTask{
//
//        @Override
//        public void run() {
//
//        }
//    }


}
