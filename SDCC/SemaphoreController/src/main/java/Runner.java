import all.master.Coordinator;
import main.java.system.Printer;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {

//	    configureLog4j("WARN");

        if(args.length == 0)
		    SpringApplication.run(Runner.class, args);
        else if(args.length == 1){
            Coordinator.getInstance().addCrossroadController(args[0], "address example");
            Coordinator.getInstance().addCrossroadController("crossroadTest2", "address example");
        }
        else{
            Printer.getInstance().print("wrong number of arguments", "red");
        }

	}


}
