package all.endpoint;

import all.endpoint.dao.CrossroadDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.system.Printer;
import org.springframework.web.bind.annotation.*;

/**
 * Author : Simone D'Aniello
 * Date :  06/03/2018.
 */

@RestController
@CrossOrigin
public class Listener {

    @RequestMapping(value = "/createCrossroad", method = RequestMethod.POST)
    public String returnSemaphoreState(@RequestBody CrossroadDao c){

        Printer.getInstance().print("got id: " + c.getId()+ " and address: " + c.getAddress(), "cyan");
//        Creator.getInstance().createCrossroad(c.getId(), c.getAddress());
        //        Printer.getInstance().print("crossroad with ID: " + crossroadDao.getID() + " arrived", "cyan");
        return "tutto ok";
    }
}
