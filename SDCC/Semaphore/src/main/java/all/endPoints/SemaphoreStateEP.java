package all.endPoints;

import all.endPoints.dao.SemaphoreDao;
import all.newArchitecture.SemaphoreClass;
import entities.Semaphore;
import entities.system.Printer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin
public class SemaphoreStateEP {

    @ResponseStatus(OK)
    @RequestMapping(value = "/addSemaphore", method = RequestMethod.POST)
    public void returnSemaphoreState(@RequestBody SemaphoreDao s){
        Printer.getInstance().print("New semaphore created", "yellow");
        Printer.getInstance().print( "latitude: " + s.getLatitude(), "cyan");
        Printer.getInstance().print( "longitude: " + s.getLongitude(), "cyan");
        Printer.getInstance().print( "Crossroad id: " + s.getCrossroad(), "cyan");
        Semaphore sem = new Semaphore();
        sem.setLatitude(s.getLatitude());
        sem.setLongitude(s.getLongitude());
        sem.setCrossroad(s.getCrossroad()[0]);
        sem.setLight(0);
        sem.setStreet(s.getAddress());

        Thread t = new Thread(() -> {
            SemaphoreClass sc = new SemaphoreClass();
            sc.registerSemaphore(sem);
        });
        t.start();
    }
}
