package all.endPoints;

import all.control.SemaphoreController;
import all.endPoints.dao.SemaphoreDao;
import main.java.Crossroad;
import main.java.system.Printer;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SemaphoreStateEP {

    @RequestMapping(value = "/addSemaphore", method = RequestMethod.POST)
    public String returnSemaphoreState(@RequestBody SemaphoreDao s){
        Printer.getInstance().print("got id: " + s.getId()+ " and address: " + s.getAddress(), "yellow");
        SemaphoreController semaphoreController = new SemaphoreController(s.getId(), s.getAddress());
        for (String c: s.getCrossroads()){
            Printer.getInstance().print("aggiungo il crossroad " + c, "yellow");
            semaphoreController.addToCrossroad(new Crossroad(c, ""));
        }

        return "tutto ok";
    }
}
