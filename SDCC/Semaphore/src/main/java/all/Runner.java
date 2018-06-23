package all;


import all.newArchitecture.SemaphoreClass;
import entities.Semaphore;
import entities.system.Printer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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


    public static void main(String[] args) throws IOException {


        Properties properties = new Properties();
        String filename = "semaphoresConfig.props";
        InputStream input = Semaphore.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("\n\nSorry, unable to find " + filename);
            return;
        }
        properties.load(input);


        String[] latitudes = properties.getProperty("latitude").split(",");
        String[] longitudes = properties.getProperty("longitude").split(",");
        String[] addresses = properties.getProperty("address").split(",");
        String[] crossroads = properties.getProperty("crossroad").split(",");
        String[] flinkDispatchers = properties.getProperty("flinkDispatcher").split(",");
        String[] controllerips = properties.getProperty("controllerip").split(",");

        for(int i = 0; i < latitudes.length; i++){
            try {
                new SemaphoreGenerator(crossroads[i], controllerips[i], latitudes[i], longitudes[i], flinkDispatchers[i], addresses[i]);
            } catch (IndexOutOfBoundsException e){
                Printer.getInstance().print("\n\n\nERROR IN PROPERTIES FILE (CHECK NUMBER OF INPUTS)\n\n\n", "red");
            }
        }

	}

	public static class SemaphoreGenerator implements Runnable {

	    private String args1;
	    private String args2;
	    private String latitude;
	    private String longitude;
	    private String address;
	    private String flinkDispatcher;

	    private SemaphoreGenerator(String controllerName, String controllerIP, String latitude, String longitude, String flinkDispatcher, String address){
	        this.args1 = controllerName;
	        this.args2 = controllerIP;
	        this.latitude = latitude;
	        this.longitude = longitude;
	        this.address=address;
	        this.flinkDispatcher=flinkDispatcher;

            (new Thread(this)).start();
        }

        @Override
        public void run() {
            Semaphore s = new Semaphore();
            s.setCrossroad(args1);
            s.setControllerIP(args2);
            s.setLatitude(latitude);
            s.setLongitude(longitude);
            s.setStreet(address);

            SemaphoreClass sc = new SemaphoreClass();
            sc.registerSemaphore(s);
        }
    }
}