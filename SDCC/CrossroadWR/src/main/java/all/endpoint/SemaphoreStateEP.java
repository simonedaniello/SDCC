package all.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SemaphoreStateEP {

    @RequestMapping(value = "/semaphoreStatus", method = RequestMethod.GET)
    public String returnSemaphoreState(){
        ObjectMapper mapper = new ObjectMapper();

//            String toReturn =  mapper.writeValueAsString(MonitorController.getInstance().getCrossroads());
//            Printer.getInstance().print(toReturn, "cyan");
            return "ciao";

    }
}
