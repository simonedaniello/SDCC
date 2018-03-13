package all.endPoints.dao;

import java.util.ArrayList;

public class SemaphoreDao {
    private String id;
    private String address;

    private ArrayList<String> crossroads;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getCrossroads() {
        return crossroads;
    }

    public void setCrossroads(ArrayList<String> crossroads) {
        this.crossroads = crossroads;
    }
}
