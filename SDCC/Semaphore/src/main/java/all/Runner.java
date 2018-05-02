package all;


import all.newArchitecture.SemaphoreClass;
import main.java.Semaphore;
import main.java.system.Printer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


@SpringBootApplication
public class Runner {

    /**
     * arguments:
     *      kafka port
     *      crossroad ID
     *      controller IP
     *
     * @param args
     */

    private static final Logger logger = LoggerFactory.getLogger(Runner.class);


    public static void main(String[] args) {




        System.out.println("args.length = " + args.length);
       // BasicConfigurator.configure();
      //  PropertyConfigurator.configure("/home/federico/IdeaProjects/SDCC/SDCC/Semaphore/src/main/java/log4j.properties");



        if(args.length == 0) {
            SpringApplication.run(Runner.class, args);
        }
        else if (args.length == 3){
            int currentLat = 10;
            int currentLong = 20;
            for(int i = 0; i<Integer.valueOf(args[0]); i++) {
                new SemaphoreGenerator(args[1], args[2], String.valueOf(currentLat), String.valueOf(currentLong));
                currentLat += 10;
                currentLong += 10;
            }
        }
        else {
            Printer.getInstance().print("error\nwrong number of arguments\nremember to use " +
                    "the next codes:\n args[0] = kafka port\nargs[1] = " +
                    "crossroadID\nargs[2] = controller ip", "red");
        }


	}

	public static class SemaphoreGenerator implements Runnable {

	    private String args1;
	    private String args2;
	    private String latitude;
	    private String longitude;

	    private SemaphoreGenerator(String args1, String args2, String latitude, String longitude){
	        this.args1 = args1;
	        this.args2 = args2;
	        this.latitude = latitude;
	        this.longitude = longitude;

            (new Thread(this)).start();
        }

        @Override
        public void run() {
            Semaphore s = new Semaphore();
            s.setCrossroad(args1);
            s.setControllerIP(args2);
            s.setLatitude(latitude);
            s.setLongitude(longitude);

//            Printer.getInstance().print("started test mode", "yellow");
//            Printer.getInstance().print("\tlatitude: " + s.getLatitude(), "yellow");
//            Printer.getInstance().print("\tlongitude: " + s.getLongitude(), "yellow");
//            Printer.getInstance().print("\tkafka port: " + s.getKafkaport(), "yellow");
//            Printer.getInstance().print("\tcorssroad id: " + s.getCrossroad(), "yellow");
//            Printer.getInstance().print("\tcontroller ip: " + s.getControllerIP(), "yellow");

            SemaphoreClass sc = new SemaphoreClass();
            sc.registerSemaphore(s);
        }
    }
}