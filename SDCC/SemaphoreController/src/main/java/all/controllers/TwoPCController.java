package all.controllers;


import all.front.FirstProducer;
import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Commit request phase
 or voting phase

 The coordinator sends a query to commit message to all cohorts and waits until it has received a reply from all cohorts.
 The cohorts execute the transaction up to the point where they will be asked to commit. They each write an entry to their undo log and an entry to their redo log.
 Each cohort replies with an agreement message (cohort votes Yes to commit), if the cohort's actions succeeded, or an abort message (cohort votes No, not to commit), if the cohort experiences a failure that will make it impossible to commit.
 Commit phase
 or Completion phase

 Success
 If the coordinator received an agreement message from all cohorts during the commit-request phase:

 The coordinator sends a commit message to all the cohorts.
 Each cohort completes the operation, and releases all the locks and resources held during the transaction.
 Each cohort sends an acknowledgment to the coordinator.
 The coordinator completes the transaction when all acknowledgments have been received.
 Failure
 If any cohort votes No during the commit-request phase (or the coordinator's timeout expires):

 The coordinator sends a rollback message to all the cohorts.
 Each cohort undoes the transaction using the undo log, and releases the resources and locks held during the transaction.
 Each cohort sends an acknowledgement to the coordinator.
 The coordinator undoes the transaction when all acknowledgements have been received.
 */


public class TwoPCController {

    private FirstProducer fp;
    private Map<String, Boolean> semaphoresFor2pc = new HashMap<>();

    public TwoPCController(FirstProducer fp){
        this.fp = fp;
    }

    public void votingPhase(ArrayList<Semaphore> semaphoreList, String greenSemaphore, String crossroadID){
        Random rand = new Random();

        Printer.getInstance().print("\n\nSTARTING VOTING PHASE\n\n", "yellow");
        for (Semaphore s : semaphoreList){
            if(rand.nextInt(100) < 90) {
                Message m = new Message(crossroadID, 301);
                if (greenSemaphore.equals(s.getID()))
                    m.setYouAreGreen(true);
                else
                    m.setYouAreGreen(false);
                fp.sendMessage("address", m, s.getID());
            }
            else {
                System.out.println("non invio il messaggio");
            }
            semaphoresFor2pc.put(s.getID(), false);
        }
    }

    private void commitPhase(){
        for (String s : semaphoresFor2pc.keySet()){
            Message m = new Message("commit", 302);
//            Printer.getInstance().print("ricorda che la commit phase sbaglia appositamente e rimanda al rollback mode", "red");
//            Message m = new Message("wrong commit", -302);
            fp.sendMessage("address", m, s);
        }
    }

    public void rollback(){
        for (String s : semaphoresFor2pc.keySet()){
            Message m = new Message("rollback", -302);
            fp.sendMessage("address", m, s);
        }
    }

    public void updateVotes(String okSemaphore){
        semaphoresFor2pc.put(okSemaphore, true);
        if(checkVotes())
            commitPhase();
    }

    private Boolean checkVotes(){
        for(String key: semaphoresFor2pc.keySet()){
            if(!semaphoresFor2pc.get(key))
                return false;
        }
        Printer.getInstance().print("\n\n\nSono arrivati tutti\n\n\n", "yellow");
        return true;
    }

}
