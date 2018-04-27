package all;

import all.controllers.Monitorer;
import main.java.system.Printer;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {

	    new Monitorer();
	}

}
