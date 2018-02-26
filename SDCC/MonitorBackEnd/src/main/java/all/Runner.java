package all;

import all.control.MonitorController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {
		MonitorController.getInstance();
		SpringApplication.run(Runner.class, args);
	}
}
