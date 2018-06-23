package all.controllers;

import entities.system.Printer;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FlinkStatusMonitorer {

    Map<String, Integer> flinkTopicsActive;
    private int TIME_CORRELATED;
    private int TIME_CYCLE;


    public FlinkStatusMonitorer(){

        Properties properties = new Properties();
        String filename = "consumer.props";
        InputStream input = FlinkStatusMonitorer.class.getClassLoader().getResourceAsStream(filename);
        if (input == null){
            System.out.println("\n\n\n\n\nSorry, unable to find " + filename);
            return;
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TIME_CORRELATED = Integer.valueOf(properties.getProperty("TIME_CORRELATED"));
        TIME_CYCLE = TIME_CORRELATED/4;

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
