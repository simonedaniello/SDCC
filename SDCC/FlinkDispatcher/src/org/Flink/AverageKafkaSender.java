package org.Flink;

import Model.GPSJsonReader;
import Model.IoTData;
import Model.SemaphoreJsonReader;
import algorithms.Welford;
import all.model.SemaphoreSensor;
import com.google.gson.Gson;
import main.java.FlinkResult;
import main.java.Message;
import main.java.system.Printer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class AverageKafkaSender {


    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final String topicname = "monitorer";
    private static final Logger log = LoggerFactory.getLogger(org.Flink.WindowTrafficData.class);
    private static int count = 0;




  /*  public void calculateAvg() throws Exception {

        INPUT_KAFKA_TOPIC = "test";
        TIME_WINDOW = 20;
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", INPUT_KAFKA_TOPIC);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011(INPUT_KAFKA_TOPIC, new SimpleStringSchema(), properties));


        System.out.println("got sources");
        DataStream<Tuple6<String, String, String, String, String, Double>> streamTuples = stream.flatMap(new IoTJson2Tuple());
        streamTuples.print();
        DataStream<Tuple2<String, Double>> averageSpeedStream = streamTuples.keyBy(new int[]{0}).timeWindow(Time.seconds((long)TIME_WINDOW),Time.seconds((long)TIME_WINDOW)).apply(new querySolver()).setParallelism(1);
        averageSpeedStream.addSink(new FlinkKafkaProducer011<>("localhost:9092", topicname, (SerializationSchema<Tuple2<String, Double>>) stringDoubleTuple2 -> {
            System.out.println("entroh");
            Gson gson = new Gson();
            String key = stringDoubleTuple2.f0;
            double value = stringDoubleTuple2.f1;
            FlinkResult flinkResult = new FlinkResult(key, value, count);
            count = 0;
            Message m = new Message("flinkDispatcher", 701);
            m.setFlinkResult(flinkResult);
            String result = gson.toJson(m);
            return result.getBytes();
        }));
        averageSpeedStream.print();
        env.execute("Window Traffic Data");

} */

    public void calculateAvg() throws Exception {

        INPUT_KAFKA_TOPIC = "semaphoresensor";
        TIME_WINDOW = 20;
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", INPUT_KAFKA_TOPIC);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011(INPUT_KAFKA_TOPIC, new SimpleStringSchema(), properties));


        System.out.println("got sources");
        DataStream<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> streamTuples = stream.flatMap(new SemaphoreJson2Tuple());
        streamTuples.print();
        DataStream<Tuple2<String, Double>> averageSpeedStream = streamTuples.keyBy(new int[]{1})
                .timeWindow(Time.seconds((long)TIME_WINDOW),Time.seconds((long)TIME_WINDOW))
                .apply(new SemaphoreQuerySolver());

        averageSpeedStream.addSink(new FlinkKafkaProducer011<>("localhost:9092", topicname, (SerializationSchema<Tuple2<String, Double>>) stringDoubleTuple2 -> {
            Gson gson = new Gson();
            String key = stringDoubleTuple2.f0;
            double value = stringDoubleTuple2.f1;
            FlinkResult flinkResult = new FlinkResult(key, value, count);
            count = 0;
            Message m = new Message("flinkDispatcher", 701);
            m.setFlinkResult(flinkResult);
            String result = gson.toJson(m);
            return result.getBytes();
        }));

        averageSpeedStream.print();
        env.execute("Window Traffic Data");

    }


/*
    Following Method executes flink for tuples generated by IoT Sensor (cars/smartphone)
 */
/*public static class querySolver implements WindowFunction<Tuple6<String, String, String, String, String, Double>, Tuple2<String, Double>, Tuple, TimeWindow> {
    public querySolver() {
    }

    @Override
    public void apply(Tuple key, TimeWindow window, Iterable<Tuple6<String, String, String, String, String, Double>> records, Collector<Tuple2<String, Double>> out) throws Exception {
        Welford welford = new Welford(); //calculate mean value
        Iterator var8 = records.iterator();

        while(var8.hasNext()) {
            Tuple6<String, String, String, String, String, Double> record = (Tuple6)var8.next();
            double speed = record.getField(5);
            welford.addElement(speed, record.getField(0));
            count++;
            System.out.println("accumuling speed");
        }

        Printer.getInstance().print(welford.getCurrent_mean().toString(),"yellow");
        for (FlinkResult f: welford.getCurrent_mean()){
            System.out.println("SemaphoreID: " + f.getKey() + ", medium speed is: " + f.getCurrent_mean());
            out.collect(new Tuple2(f.getKey(), f.getCurrent_mean()));
            //welford.resetIndexes(f);
        }

    }
} */


/*
    Following Class executes flink for tuples generated by semaphores' sensor.
 */

    public static class SemaphoreQuerySolver implements WindowFunction<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>, Tuple2<String, Double>, Tuple, TimeWindow> {
        public SemaphoreQuerySolver() {
        }

        @Override
        public void apply(Tuple key, TimeWindow window, Iterable<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> records, Collector<Tuple2<String, Double>> out) throws Exception {
            Iterator var8 = records.iterator();
            Welford welford = new Welford(key.getField(0)); //calculate mean value
            //Printer.getInstance().print(key.toString().substring(1,key.toString().length() - 1),"blue");

            while(var8.hasNext()) {
                Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean> record = (Tuple11<String, String, String, String, String, Int, Double, Double, Boolean, Boolean, Boolean>) var8.next();
                double speed = record.getField(7);
                welford.addElement(speed);
                count++;
                System.out.println("accumuling speed");
            }

            FlinkResult f = welford.getCurrent_mean();
            System.out.println("SemaphoreID: " + f.getKey() + ", medium speed is: " + f.getValue());
            out.collect(new Tuple2(f.getKey(), f.getValue()));

        }
    }

/*public static class IoTJson2Tuple implements FlatMapFunction<String, Tuple6<String, String, String, String, String, Double>> {
    public IoTJson2Tuple() {
    }

    @Override
    public void flatMap(String jsonString, Collector<Tuple6<String, String, String, String, String, Double>> out) throws Exception {
        ArrayList<IoTData> recs = GPSJsonReader.getIoTDatas(jsonString);
        Iterator irecs = recs.iterator();

        while(irecs.hasNext()) {
            IoTData record = (IoTData)irecs.next();
            Tuple6<String, String, String, String, String, Double> tp9 = new Tuple6();
            tp9.setField(INPUT_KAFKA_TOPIC, 0);
            tp9.setField(record.getTimestamp(), 1);
            tp9.setField(record.getVehicleId(), 2);
            tp9.setField(record.getLatitude(), 3);
            tp9.setField(record.getLongitude(), 4);
            tp9.setField(record.getSpeed(), 5);
            out.collect(tp9);
        }

    }
} */

    public static class SemaphoreJson2Tuple implements FlatMapFunction<String, Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> {
        public SemaphoreJson2Tuple() {
        }

        @Override
        public void flatMap(String jsonString, Collector<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> out) throws Exception {
            ArrayList<SemaphoreSensor> recs = SemaphoreJsonReader.getSemaphoreDatas(jsonString);
            Iterator irecs = recs.iterator();

            while(irecs.hasNext()) {
                SemaphoreSensor record = (SemaphoreSensor) irecs.next();
                Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean> tp11 = new Tuple11();
                tp11.setField(record.getCrossroadID(), 0);
                tp11.setField(record.getSemaphoreID(), 1);
                tp11.setField(record.getLatitude(), 2);
                tp11.setField(record.getLongitude(), 3);
                tp11.setField(record.getTimestamp(), 4);
                tp11.setField(record.getGreenDuration(), 5);
                tp11.setField(record.getCarsInTimeUnit(), 6);
                tp11.setField(record.getMeanSpeed(), 7 );
                tp11.setField(record.isGreenWorking(),8 );
                tp11.setField(record.isYellowWorking(),9 );
                tp11.setField(record.isRedWorking(),10);



                out.collect(tp11);
            }

        }
    }}
