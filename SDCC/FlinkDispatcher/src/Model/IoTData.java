package Model;

import java.io.Serializable;

public class IoTData implements Serializable {
    private String vehicleId;
    private String timestamp;
    private String latitude;
    private String longitude;
    private double speed;
    private String jsonString;

    public String getJsonString() {
        return this.jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public IoTData() {
    }

    public IoTData(String vehicleId, String latitude, String longitude, String timestamp, double speed) {
        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }

    public String getVehicleId() {
        return this.vehicleId;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public double getSpeed() {
        return this.speed;
    }
}
