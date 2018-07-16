package all;

import all.master.Coordinator;

import entities.Semaphore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {

        Thread t = new Thread(() -> {
            SpringApplication.run(Runner.class, args);
        });
        t.start();

        Properties properties = new Properties();
        String filename = "controllerConfiguration.props";
        InputStream input = Semaphore.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("\n\nSorry, unable to find " + filename);
            return;
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] crossroads = properties.getProperty("crossroadid").split(",");
        String[] addresses = properties.getProperty("crossroadaddress").split(",");
        //for(int i = 0; i<crossroads.length; i++){
        for(int i = 0; i<50; i++){
            Coordinator.getInstance().addCrossroadController(crossroads[i].replace(" ", "").replace("/", ""), addresses[i]);
        }

	}


}
