package all.endpoint;

import all.controllers.CrossroadController;

/**
 * Author : Simone D'Aniello
 * Date :  06/03/2018.
 */
public class Creator {
    private static Creator instance = new Creator();

    private Creator(){}

    public static Creator getInstance() {
        return instance;
    }

    void createCrossroad(String id, String street){
        new CrossroadController(id, street);
    }
}
