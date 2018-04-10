package all;

import all.control.SemaphoreController;
import main.java.Crossroad;
import main.java.system.Printer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Runner {

	public static void main(String[] args) {
//        try {
//            generalSelf();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        SpringApplication.run(Runner.class, args);
		SemaphoreController semaphoreController1 = new SemaphoreController("sem0001", "via libera");
		Crossroad c = new Crossroad("crossroadexample", "via ai test");
		semaphoreController1.addToCrossroad(c);
		Printer.getInstance().print("aggiungo semaforo 2", "yellow");
		SemaphoreController semaphoreController2 = new SemaphoreController("sem0002", "via libera");
		semaphoreController2.addToCrossroad(c);
	}

	private static void generalSelf() throws InterruptedException  {
		SemaphoreController semaphoreController1 = new SemaphoreController("sem0001", "via libera");
		SemaphoreController semaphoreController2 = new SemaphoreController("sem0002", "via vai");
		SemaphoreController semaphoreController3 = new SemaphoreController("sem0003", "via dotto");

		Crossroad c = new Crossroad("crossroad example", "via ai test");
		semaphoreController1.addToCrossroad(c);
		//TimeUnit.SECONDS.sleep(1);
		semaphoreController3.addToCrossroad(c);
		//TimeUnit.SECONDS.sleep(1);
		semaphoreController2.addToCrossroad(c);
		//TimeUnit.SECONDS.sleep(1);
		semaphoreController1.removeCrossroad(c);

		TimeUnit.SECONDS.sleep(2);
		semaphoreController1.printState();
		semaphoreController2.printState();
		semaphoreController3.printState();

		TimeUnit.SECONDS.sleep(20);
		semaphoreController1.addToCrossroad(c);



		//System.exit(0);
	}
}
