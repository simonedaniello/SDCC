package all;

import all.control.MonitorController;
import all.db.MongoDataStore;
import com.mongodb.Mongo;
import main.java.system.Printer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.print.Printable;
import java.net.UnknownHostException;
import java.util.ArrayList;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {
//        try {
//            Printer.getInstance().print("searching crossroad test:\n" + MongoDataStore.getInstance().searchCrossroad("crossroadtest"), "yellow");
//            Printer.getInstance().print("searching cambiaAssolutamente:\n" + MongoDataStore.getInstance().searchCrossroad("cambiaAssolutamente"), "yellow");
//            ArrayList<String> controllers = MongoDataStore.getInstance().getAllControllers();
//            ArrayList<String> crossroads = MongoDataStore.getInstance().getAllCrossroads();
//            Printer.getInstance().print("controllers: ", "green");
//            for(String s : controllers){
//                System.out.println(s);
//            }
//            Printer.getInstance().print("crossroads: ", "green");
//            for(String s : crossroads){
//                System.out.println(s);
//            }
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
		SpringApplication.run(Runner.class, args);
		MonitorController.getInstance();
	}
}
