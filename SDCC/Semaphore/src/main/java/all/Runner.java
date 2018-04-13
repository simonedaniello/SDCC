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

	private static void generateLatitudeLongitude(Semaphore s)  {
        s.setLatitude("10");
        s.setLongitude("20");
	}

	public static class SemaphoreGenerator implements Runnable {

	    private String args1;
	    private String args2;
	    private String latitude;
	    private String longitude;

	    public SemaphoreGenerator(String args1, String args2, String latitude, String longitude){
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

            SemaphoreClass.getInstance().registerSemaphore(s);
        }
    }
}
