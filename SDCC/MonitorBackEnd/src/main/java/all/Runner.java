package all;

import all.control.MonitorController;
import all.db.MongoDataStore;
import com.mongodb.Mongo;
import entities.system.Printer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.print.Printable;
import java.net.UnknownHostException;
import java.util.ArrayList;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {
		SpringApplication.run(Runner.class, args);
		MonitorController.getInstance();
	}
}
