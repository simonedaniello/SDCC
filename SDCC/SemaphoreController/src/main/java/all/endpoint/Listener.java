package all.endpoint;

import all.endpoint.dao.CrossroadDao;
import all.master.Coordinator;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.system.Printer;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

/**
 * Author : Simone D'Aniello
 * Date :  06/03/2018.
 */

@RestController
@CrossOrigin
public class Listener {

    @ResponseStatus(OK)
    @RequestMapping(value = "/createCrossroad", method = RequestMethod.POST)
    public void returnSemaphoreState(@RequestBody CrossroadDao c){


        Thread t = new Thread(() -> {
            Coordinator.getInstance().addCrossroadController(c.getId(), c.getAddress());
        });
        t.start();

    }

    @RequestMapping(value = "/semaphoreStatus", method = RequestMethod.GET)
    public void returnSemaphoreState(){
        ObjectMapper mapper = new ObjectMapper();

//            String toReturn =  mapper.writeValueAsString(MonitorController.getInstance().getCrossroads());
//            Printer.getInstance().print(toReturn, "cyan");


    }
}
