package Model;

import entities.Crossroad;
import entities.Semaphore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author : Simone D'Aniello
 * Date :  21-Feb-18.
 */
public class Message implements Serializable {

    private String ID;
    private FlinkResult flinkResult;
    private int code;



    /*
     *
     * code values:
     *   1: semaphore added to crossroad
     *   -1: removing semaphore from crossroad
     *   400 : monitor message
     *   401 : request of the crossroad monitor
     *   10 : get list of semaphores from crossroad
     *   500 : send status to monitorBackEnd
     *
     */


    public Message(String ID, int code) {
        setID(ID);
        setCode(code);
    }

    public Message() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public FlinkResult getFlinkResult() {
        return flinkResult;
    }

    public void setFlinkResult(FlinkResult flinkResult) {
        this.flinkResult = flinkResult;
    }

}
