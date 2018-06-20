package Model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class SemaphoreJsonReader {

    public SemaphoreJsonReader() {}


    public static ArrayList<SemaphoreSensor> getSemaphoreDatas(String jsonString){


        ArrayList<SemaphoreSensor> semaphoreSensors = new ArrayList();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonString);
        if (element.isJsonObject()) {
            JsonObject jsonRecord = element.getAsJsonObject();
            SemaphoreSensor data = new SemaphoreSensor();
            data.setCrossroadID(jsonRecord.get("crossroadID").getAsString());
            data.setSemaphoreID(jsonRecord.get("semaphoreID").getAsString());
            data.setLatitude(jsonRecord.get("latitude").getAsString());
            data.setLongitude(jsonRecord.get("longitude").getAsString());
            data.setTimestamp(jsonRecord.get("timestamp").getAsString());
            data.setGreenDuration(jsonRecord.get("greenDuration").getAsInt());
            data.setCarsInTimeUnit(jsonRecord.get("carsInTimeUnit").getAsDouble());
            data.setMeanSpeed(jsonRecord.get("meanSpeed").getAsDouble());
            data.setGreenWorking(jsonRecord.get("greenWorking").getAsBoolean());
            data.setYellowWorking(jsonRecord.get("yellowWorking").getAsBoolean());
            data.setRedWorking(jsonRecord.get("redWorking").getAsBoolean());

            semaphoreSensors.add(data);
        } else {
            System.out.println("Not a valid Json Element");
        }


        return semaphoreSensors;
    }
}
