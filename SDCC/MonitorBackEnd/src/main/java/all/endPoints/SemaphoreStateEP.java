package all.endPoints;

import all.control.MonitorController;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.system.Printer;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
public class SemaphoreStateEP {

    @RequestMapping(value = "/semaphoreStatus", method = RequestMethod.GET)
    public String returnSemaphoreState(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String toReturn =  mapper.writeValueAsString(MonitorController.getInstance());
            Printer.getInstance().print(toReturn, "cyan");
            return toReturn;
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

}
