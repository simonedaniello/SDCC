package org.Flink;

import Model.GPSJsonReader;
import Model.IoTData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowTrafficData {
    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final Logger log = LoggerFactory.getLogger(WindowTrafficData.class);

    public WindowTrafficData() {
    }

    public static void main(String[] args) throws Exception {
        INPUT_KAFKA_TOPIC = "test";
        TIME_WINDOW = 10;
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "test");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer09("test", new SimpleStringSchema(), properties));
        System.out.println("got sources");
        DataStream<Tuple6<String, String, String, String, String, Double>> streamTuples = stream.flatMap(new WindowTrafficData.Json2Tuple());
        streamTuples.print();
        DataStream<Tuple2<String, Double>> averageSpeedStream = streamTuples.keyBy(new int[]{0}).timeWindow(Time.seconds((long)TIME_WINDOW), Time.seconds((long)TIME_WINDOW)).apply(new WindowTrafficData.AverageSpeed());
        averageSpeedStream.print();
        env.execute("Window Traffic Data");
    }

    public static class AverageSpeed implements WindowFunction<Tuple6<String, String, String, String, String, Double>, Tuple2<String, Double>, Tuple, TimeWindow> {
        public AverageSpeed() {
        }

        public void apply(Tuple key, TimeWindow window, Iterable<Tuple6<String, String, String, String, String, Double>> records, Collector<Tuple2<String, Double>> out) throws Exception {
            int count = 0;
            double speedAccumulator = 0.0D;
            Iterator var8 = records.iterator();

            while(var8.hasNext()) {
                Tuple6<String, String, String, String, String, Double> record = (Tuple6)var8.next();
                double speed = (Double)record.getField(5);
                System.out.println("speed is: " + speed);
                ++count;
                speedAccumulator += speed;
                System.out.println("accumuling speed");
            }

            double averageSpeed = speedAccumulator / (double)count;
            out.collect(new Tuple2(WindowTrafficData.INPUT_KAFKA_TOPIC, averageSpeed));
        }
    }

    public static class Json2Tuple implements FlatMapFunction<String, Tuple6<String, String, String, String, String, Double>> {
        public Json2Tuple() {
        }

        public void flatMap(String jsonString, Collector<Tuple6<String, String, String, String, String, Double>> out) throws Exception {
            ArrayList<IoTData> recs = GPSJsonReader.getIoTDatas(jsonString);
            Iterator irecs = recs.iterator();

            while(irecs.hasNext()) {
                IoTData record = (IoTData)irecs.next();
                Tuple6<String, String, String, String, String, Double> tp9 = new Tuple6();
                tp9.setField(WindowTrafficData.INPUT_KAFKA_TOPIC, 0);
                tp9.setField(record.getTimestamp(), 1);
                tp9.setField(record.getVehicleId(), 2);
                tp9.setField(record.getLatitude(), 3);
                tp9.setField(record.getLongitude(), 4);
                tp9.setField(record.getSpeed(), 5);
                out.collect(tp9);
            }

        }
    }
}
