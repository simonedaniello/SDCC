package all.endPoints;

import all.endPoints.dao.SemaphoreDao;
import all.newArchitecture.SemaphoreClass;
import entities.Semaphore;
import entities.system.Printer;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SemaphoreStateEP {

    @RequestMapping(value = "/addSemaphore", method = RequestMethod.POST)
    public String returnSemaphoreState(@RequestBody SemaphoreDao s){
 /*       Printer.getInstance().print("New semaphore created", "yellow");
        Printer.getInstance().print( "latitude: " + s.getLatitude(), "cyan");
        Printer.getInstance().print( "longitude: " + s.getLongitude(), "cyan");
        Printer.getInstance().print( "Crossroad id: " + s.getCrossroad(), "cyan");*/
        Semaphore sem = new Semaphore();
        sem.setLatitude(s.getLatitude());
        sem.setLongitude(s.getLongitude());
        sem.setCrossroad(s.getCrossroad());
        sem.setLight(0);

        SemaphoreClass sc = new SemaphoreClass();
        sc.registerSemaphore(sem);

        return "OK";
    }
}
