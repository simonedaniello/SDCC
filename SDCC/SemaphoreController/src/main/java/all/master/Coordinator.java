package all.master;


import all.controllers.CrossroadController;

import java.util.ArrayList;

public class Coordinator {

    private static Coordinator instance = new Coordinator();
    private ArrayList<CrossroadController> crossroadControllerlist = new ArrayList<>();

    private Coordinator(){}

    public static Coordinator getInstance() {
        return instance;
    }

    public void addCrossroadController(String crossroadControllerID, String crossroadControllerAddress){
        CrossroadController cc = new CrossroadController(crossroadControllerID, crossroadControllerAddress);
        crossroadControllerlist.add(cc);
    }

}
