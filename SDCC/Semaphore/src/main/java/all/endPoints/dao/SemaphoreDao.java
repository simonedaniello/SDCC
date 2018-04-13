package all.endPoints.dao;

import java.util.ArrayList;

public class SemaphoreDao {
    private String latitude;
    private String longitude;
    private String crossroad;
    private String address;



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCrossroad() {
        return crossroad;
    }

    public void setCrossroad(String crossroad) {
        this.crossroad = crossroad;
    }
}
