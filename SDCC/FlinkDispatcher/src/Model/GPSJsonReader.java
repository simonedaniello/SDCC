package Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.java.tuple.Tuple8;

public class GPSJsonReader {
    public static final int KEY = 0;
    public static final int RECORDED_TIMESTAMP = 1;
    public static final int VEHICLE_ID = 2;
    public static final int LAT = 3;
    public static final int LON = 4;
    public static final int SPEED = 5;

    public GPSJsonReader() {
    }

    public static ArrayList<IoTData> getIoTDatas(String jsonString) {
        ArrayList<IoTData> IoTDatas = new ArrayList();
        System.out.println(jsonString);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonString);
        if (element.isJsonObject()) {
            JsonObject jsonRecord = element.getAsJsonObject();
            IoTData newcar = new IoTData();
            newcar.setJsonString(jsonString);
            newcar.setTimestamp(jsonRecord.get("timestamp").getAsString());
            newcar.setVehicleId(jsonRecord.get("vehicleId").getAsString());
            newcar.setLatitude(jsonRecord.get("latitude").getAsString());
            newcar.setLongitude(jsonRecord.get("longitude").getAsString());
            newcar.setSpeed(jsonRecord.get("speed").getAsDouble());
            IoTDatas.add(newcar);
        } else {
            System.out.println("Not a valid Json Element");
        }

        return IoTDatas;
    }





    public static IoTData getIoTData(String jsonString) {
        System.out.println(jsonString);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonString);
        IoTData newcar = new IoTData();

        if (element.isJsonObject()) {
            JsonObject jsonRecord = element.getAsJsonObject();
            newcar.setJsonString(jsonString);
            newcar.setTimestamp(jsonRecord.get("timestamp").getAsString());
            newcar.setVehicleId(jsonRecord.get("vehicleId").getAsString());
            newcar.setLatitude(jsonRecord.get("latitude").getAsString());
            newcar.setLongitude(jsonRecord.get("longitude").getAsString());
            newcar.setSpeed(jsonRecord.get("speed").getAsDouble());
        } else {
            System.out.println("Not a valid Json Element");
            return null;
        }

        return newcar;
    }



    public static List<Tuple8<String, String, String, String, String, String, String, String>> getGpsTuples(String jsonString) {
        ArrayList<Tuple8<String, String, String, String, String, String, String, String>> IoTDatas = new ArrayList();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonString);
        Tuple8<String, String, String, String, String, String, String, String> tp8 = null;
        if (element.isJsonArray()) {
            JsonArray jsonRecords = element.getAsJsonArray();

            for(int i = 0; i < jsonRecords.size(); ++i) {
                tp8 = new Tuple8();
                JsonObject jsonRecord = jsonRecords.get(i).getAsJsonObject();
                tp8.setField(jsonRecord.get("recorded_timestamp").getAsString(), 1);
                tp8.setField(jsonRecord.get("vehicle_id").getAsString(), 2);
                tp8.setField(jsonRecord.get("lat").getAsString(), 3);
                tp8.setField(jsonRecord.get("lon").getAsString(), 4);
                tp8.setField(jsonRecord.get("speed").getAsString(), 5);
                IoTDatas.add(tp8);
            }
        }

        return IoTDatas;
    }
}
