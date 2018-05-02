package org.Flink;

import Model.GPSJsonReader;
import Model.IoTData;
import algorithms.QuantileEstimator;
import algorithms.Welford;
import com.google.gson.Gson;
import main.java.FlinkResult;
import main.java.Message;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple6;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class QuantileKafkaSender {


    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final String topicname = "monitorer";
    private static final Logger log = LoggerFactory.getLogger(WindowTrafficData.class);
    private static long count = 0;



    public void calculateQuantiles() throws Exception {

        INPUT_KAFKA_TOPIC = "test";
        TIME_WINDOW = 20;
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "test");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011("test", new SimpleStringSchema(), properties));


        System.out.println("got sources");
        DataStream<Tuple6<String, String, String, String, String, Double>> streamTuples = stream.flatMap(new Json2Tuple());
        streamTuples.print();
        DataStream<Tuple2<String, Double>> averageSpeedStream = streamTuples.keyBy(new int[]{0}).timeWindow(Time.seconds((long)TIME_WINDOW),Time.seconds((long)TIME_WINDOW)).apply(new querySolver()).setParallelism(1);
        averageSpeedStream.addSink(new FlinkKafkaProducer011<>("localhost:9092", topicname, (SerializationSchema<Tuple2<String, Double>>) stringDoubleTuple2 -> {
            Gson gson = new Gson();
            String key = stringDoubleTuple2.f0;
            double value = stringDoubleTuple2.f1;
            FlinkResult flinkResult = new FlinkResult(key, value, count);
            count = 0;
            Message m = new Message("flinkDispatcher", 702);
            m.setFlinkResult(flinkResult);
            String result = gson.toJson(m);
            return result.getBytes();
        }));
        averageSpeedStream.print();
        env.execute("Window Traffic Data");

    }

    public static class querySolver implements
            WindowFunction<Tuple6<String, String, String, String, String, Double>, Tuple2<String, Double>, Tuple, TimeWindow> {
        public querySolver() {
        }

        @Override
        public void apply(Tuple key, TimeWindow window, Iterable<Tuple6<String,
                String, String, String, String, Double>> records, Collector<Tuple2<String, Double>> out)
                throws Exception {
            QuantileEstimator qe = new QuantileEstimator(5); //estimates quantiles
            Iterator var8 = records.iterator();

            while(var8.hasNext()) {
                Tuple6<String, String, String, String, String, Double> record = (Tuple6)var8.next();
                double speed = record.getField(5);
                qe.add(speed);
                count++;
                System.out.println("accumuling speed");
            }

            List<Double> quantiles = qe.getQuantiles();
            System.out.println(qe.getQuantiles());
            System.out.println("median of speed is:");

            //out.collect(new Tuple2(INPUT_KAFKA_TOPIC, quantiles.get(2)));
            qe.clear();
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
                tp9.setField(INPUT_KAFKA_TOPIC, 0);
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
