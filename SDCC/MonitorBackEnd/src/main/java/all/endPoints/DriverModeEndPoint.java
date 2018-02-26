package all.endPoints;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class DriverModeEndPoint {

    @RequestMapping(value = "/semaphoreStatus", method = RequestMethod.GET)
    public String getComandeAttive(){
        return "Ciao questo test funziona";
    }

}
