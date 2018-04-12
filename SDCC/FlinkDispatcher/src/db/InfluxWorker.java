package db;

import main.java.system.Printer;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxWorker {

    private static InfluxDB influxDB;
    private static String rpName;
    private static String dbName;

    public static void main(String[] args) throws InterruptedException {

        connectToInflux();
        dbName = "toDelete2";

        createDB(dbName);
//        influxDB.setDatabase(dbName);
        addPolicy(dbName);

        ArrayList<String> fields1 = new ArrayList<>();
        ArrayList<String> values1 = new ArrayList<>();
        fields1.add("campoTest1");
        fields1.add("campoTest2");
        fields1.add("campoTest3");

        values1.add("1valoreTest1");
        values1.add("1valoreTest2");
        values1.add("1valoreTest3");

        ArrayList<String> values2 = new ArrayList<>();
        values2.add("2valoreTest1");
        values2.add("2valoreTest2");
        values2.add("2valoreTest3");

        ArrayList<String> values3 = new ArrayList<>();
        values3.add("3valoreTest1");
        values3.add("3valoreTest2");
        values3.add("3valoreTest3");

        addDataToDB("misurazione", fields1, values1);
        TimeUnit.MILLISECONDS.sleep(3);
        addDataToDB("misurazione", fields1, values3);
        TimeUnit.MILLISECONDS.sleep(3);
        addDataToDB("misurazione", fields1, values2);
        TimeUnit.MILLISECONDS.sleep(10);


        executeQuery(dbName, "SELECT * FROM misurazione");

        deleteDB("toDelete2");

        influxDB.close();




//        influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
//        String dbName = "aTimeSeries";
//        influxDB.createDatabase(dbName);
//        influxDB.setDatabase(dbName);
//        rpName = "aRetentionPolicy";
//        influxDB.createRetentionPolicy(rpName, dbName, "30d", "30m", 2, true);
//        influxDB.setRetentionPolicy(rpName);
//
//        influxDB.enableBatch(BatchOptions.DEFAULTS);
//
//        influxDB.write(Point.measurement("cpu")
//                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
//                .addField("idle", 90L)
//                .addField("user", 9L)
//                .addField("system", 1L)
//                .build());
//
//        influxDB.write(Point.measurement("disk")
//                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
//                .addField("used", 80L)
//                .addField("free", 1L)
//                .build());
//
//        Query query = new Query("SELECT idle FROM cpu", dbName);
//        influxDB.query(query);
//        influxDB.dropRetentionPolicy(rpName, dbName);
//        influxDB.deleteDatabase(dbName);
//        influxDB.close();
//
//        System.out.println("fine");



    }

    public static void connectToInflux(){
        influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
    }

    public static void addPolicy(String dbName){
        rpName = "aRetentionPolicy";
        influxDB.createRetentionPolicy(rpName, dbName, "30d", "30m", 1, true);
        influxDB.setRetentionPolicy(rpName);

        influxDB.enableBatch(BatchOptions.DEFAULTS.exceptionHandler(
                (failedPoints, throwable) -> { Printer.getInstance().print("error:" + throwable, "red"); })
        );

    }

    public static void createDB(String dbName){

        influxDB.createDatabase(dbName);
        influxDB.setDatabase(dbName);
    }

    public static void addDataToDB(String measurement, ArrayList<String> fields, ArrayList<String> values){
        if (fields.size() == values.size()){
            Point.Builder b = Point.measurement(measurement);

            b.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            for(int i = 0; i < fields.size(); i++){
                b.addField(fields.get(i), values.get(i));
            }
//            b.addField("idle", 90L)
//            b.addField("user", 9L)
//            b.addField("system", 1L)

            influxDB.write(dbName, rpName, b.build());
            System.out.println("chiamata eseguita");
        }
    }

    public static void deleteDB(String dbName){
        influxDB.dropRetentionPolicy(rpName, dbName);
        influxDB.deleteDatabase(dbName);
//        influxDB.close();
    }

    public static void executeQuery(String dbName, String query){

        Query q = new Query(query, dbName);
        List<QueryResult.Result> results = influxDB.query(q).getResults();
        for(QueryResult.Result r : results){

//            Printer.getInstance().print(r.toString(), "yellow");
//            Printer.getInstance().print("r.getSeries.size = " +  r.getSeries().size(), "yellow");
//            Printer.getInstance().print("r.getSeries(0).values = " +  r.getSeries().get(0).getValues(), "yellow");
//            Printer.getInstance().print( "r.getSeries(0).values.get(0) = " + r.getSeries().get(0).getValues().get(0), "yellow");
//            Printer.getInstance().print( "r.getSeries(0).values.get(0).get(0) = " + r.getSeries().get(0).getValues().get(0).get(0), "yellow");



            //--------------------PORCATA-------------------------------------------------------



            if(r.getSeries() == null){executeQuery(dbName, query);}
            int v = 0;
            if (r.getSeries() != null) {
                for (List row : r.getSeries().get(0).getValues()) {
                    Printer.getInstance().print("Item : " + v, "blue");
                    v++;
                    for (int i = 0; i < r.getSeries().get(0).getColumns().size(); i++) {
                        Printer.getInstance().print("\t" + r.getSeries().get(0).getColumns().get(i) + ": " + row.get(i), "blue");
                    }
                }

            }
        }

    }
}
