package all;


import all.newArchitecture.SemaphoreClass;
import main.java.Semaphore;
import main.java.system.Printer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
	public static void main(String[] args) {

        System.out.println("args.length = " + args.length);

        if(args.length == 0) {
            SpringApplication.run(Runner.class, args);
        }
        else if (args.length == 3){
            Semaphore s = new Semaphore();
            s.setCrossroad(args[1]);
            s.setControllerIP(args[2]);
            generateLatitudeLongitude(s);

            Printer.getInstance().print("started test mode", "yellow");
            Printer.getInstance().print("\tlatitude: " + s.getLatitude(), "yellow");
            Printer.getInstance().print("\tlongitude: " + s.getLongitude(), "yellow");
            Printer.getInstance().print("\tkafka port: " + s.getKafkaport(), "yellow");
            Printer.getInstance().print("\tcorssroad id: " + s.getCrossroad(), "yellow");
            Printer.getInstance().print("\tcontroller ip: " + s.getControllerIP(), "yellow");

            SemaphoreClass.getInstance().registerSemaphore(s);
        }
        else {
            Printer.getInstance().print("error\nwrong number of arguments\nremember to use " +
                    "the next codes:\n args[0] = kafka port\nargs[1] = " +
                    "crossroadID\nargs[2] = controller ip", "red");
        }




	}

	private static void generateLatitudeLongitude(Semaphore s)  {
        s.setLatitude("10");
        s.setLongitude("20");
	}
}
