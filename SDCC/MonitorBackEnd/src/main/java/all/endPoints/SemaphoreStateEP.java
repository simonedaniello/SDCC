package all.endPoints;

import all.control.MonitorController;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.Crossroad;
import main.java.Semaphore;
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
            String toReturn =  mapper.writeValueAsString(MonitorController.getInstance().getCrossroads());
//            Printer.getInstance().print(toReturn, "cyan");
            return toReturn;
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }


    @RequestMapping(value = "/addCrossroad", method = RequestMethod.GET)
    public String addCrossroad(Crossroad crossroad){
        if(MonitorController.getInstance().flinkAddCrossroad(crossroad))
            return "OK";
        else
            return "ERROR";
    }

    @RequestMapping(value = "/addSemaphore", method = RequestMethod.GET)
    public String addSemaphore(Semaphore semaphore){
        if(MonitorController.getInstance().flinkAddSemaphore(semaphore))
            return "OK";
        else
            return "ERROR";
    }

    @RequestMapping(value = "/getCrossroadInfo", method = RequestMethod.GET)
    public String getCrossroadInfo(Crossroad crossroad){
        return MonitorController.getInstance().flinkGetCrossroadSituation(crossroad);
    }

    @RequestMapping(value = "/getGeneralInfo", method = RequestMethod.GET)
    public String getGeneralInfo(){
        return MonitorController.getInstance().flinkGetGeneralSituation();

    }

    @RequestMapping(value = "/getQueries", method = RequestMethod.GET)
    public String getQueries(){
        return MonitorController.getInstance().flinkGetQueries();
    }

}
