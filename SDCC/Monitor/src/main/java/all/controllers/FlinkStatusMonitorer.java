package all.controllers;


import main.java.Message;
import main.java.Semaphore;
import main.java.system.Printer;

import java.util.*;

public class FlinkStatusMonitorer {

    Map<String, Integer> flinkTopicsActive;
    private final int TIME_CORRELATED = 300000;
    private final int TIME_CYCLE = TIME_CORRELATED/4;


    public FlinkStatusMonitorer(){
        this.flinkTopicsActive = new HashMap<>();
        Timer timer = new Timer();
        timer.schedule(new FlinkStatusMonitorer.TimerClass(), TIME_CYCLE, TIME_CYCLE);
    }

    public void updateTimeout(String flinkTopicID){
        Printer.getInstance().print("eseguo l'update del flinkDispatcher " + flinkTopicID, "cyan");
        flinkTopicsActive.put(flinkTopicID, TIME_CORRELATED);
    }

    private void timeoutExpired(String flinkTopicID){
        flinkTopicsActive.remove(flinkTopicID);
        Printer.getInstance().print("\n\n\n\nFLINK DISPATCHER CADUTO. IMPLEMENTA UN RIMEDIO\n\n\n\n", "red");
    }

    private class TimerClass extends TimerTask {
        @Override
        public void run() {
            ArrayList<String> toRemove = new ArrayList<>();
            for(String key: flinkTopicsActive.keySet()){
                if (flinkTopicsActive.get(key) < TIME_CYCLE)
                    toRemove.add(key);
                else
                    flinkTopicsActive.put(key, flinkTopicsActive.get(key) - TIME_CYCLE);
            }
            for(String r: toRemove){
                timeoutExpired(r);
            }
        }
    }



}
