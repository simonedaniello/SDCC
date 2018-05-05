package org.Flink;

import Model.SemaphoreJsonReader;
import all.model.SemaphoreSensor;
import com.google.gson.Gson;
import main.java.FlinkResult;
import main.java.Message;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class NewAverageKafkaSender {


    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final String topicname = "monitorer";
    private static final Logger log = LoggerFactory.getLogger(org.Flink.WindowTrafficData.class);



    public void calculateAvg() throws Exception {

        INPUT_KAFKA_TOPIC = "semaphoresensor";
        TIME_WINDOW = 10;
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", INPUT_KAFKA_TOPIC);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011(INPUT_KAFKA_TOPIC, new SimpleStringSchema(), properties));

        System.out.println("got sources");
       // DataStream<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> streamTuples = stream.flatMap(new SemaphoreJson2Tuple());
        DataStream<Tuple2<String, Double>> streamTuples = stream.flatMap(new SemaphoreJson2Tuple());

        streamTuples.print();
        DataStream<Tuple3<String, Double, Integer>> averageSpeedStream = streamTuples
                .keyBy(new int[]{0})
                .timeWindow(Time.seconds((long)TIME_WINDOW))
                .aggregate((AggregateFunction<Tuple2<String, Double>, Tuple3 <String, Double, Double>, Tuple3<String, Double, Integer>>) new AverageAggregate());


        //write to another kafka topic
       averageSpeedStream.addSink(new FlinkKafkaProducer011<>("localhost:9092", topicname, (SerializationSchema<Tuple3<String, Double, Integer>>) stringDoubleTuple3 -> {
            Gson gson = new Gson();
            String key = stringDoubleTuple3.f0;
            double value = stringDoubleTuple3.f1;
            int count = stringDoubleTuple3.f2;
            FlinkResult flinkResult = new FlinkResult(key, value, count);
            Message m = new Message("flinkDispatcher", 70115);
            m.setFlinkResult(flinkResult);
            String result = gson.toJson(m);
            return result.getBytes();
        }));

        averageSpeedStream.print();
        env.execute("Window Traffic Data");

    }





/*
    Following Class executes flink for tuples generated by semaphores' sensor.
 */

    private static class AverageAggregate implements
            AggregateFunction<Tuple2<String, Double>, Tuple3<String,Double, Double>, Tuple3<String, Double, Integer>> {

        @Override
        public Tuple3<String, Double, Double> createAccumulator() {
            return new Tuple3<>("initializer", 0.0, 0.0);
        }

        @Override
        public Tuple3<String,Double, Double> add(Tuple2<String, Double> value, Tuple3 <String, Double, Double> accumulator) {
//            double x = accumulator.f1 + value.f1;
//            System.out.println("accumulator più value is: " + x);
//            System.out.println("Index is: " + accumulator.f2);
            return new Tuple3<>(value.f0, accumulator.f1 + value.f1, accumulator.f2 + 1.0);
        }

        @Override
        public Tuple3<String, Double, Integer> getResult(Tuple3 <String, Double, Double> accumulator) {
            System.out.println("get result");
            return new Tuple3<>(accumulator.f0, accumulator.f1 / accumulator.f2, accumulator.f2.intValue());        }

        @Override
        public Tuple3 <String, Double, Double> merge(Tuple3 <String, Double, Double> a, Tuple3 <String, Double, Double> b ) {
            return new Tuple3 <String, Double, Double>(a.f0, a.f1 + b.f1, a.f2 + b.f2);
        }
    }


    public static class SemaphoreJson2Tuple implements FlatMapFunction<String, Tuple2<String,Double>> {
        public SemaphoreJson2Tuple() {
        }

        @Override
        public void flatMap(String jsonString, Collector<Tuple2<String, Double>> out) {
            ArrayList<SemaphoreSensor> recs = SemaphoreJsonReader.getSemaphoreDatas(jsonString);
            Iterator irecs = recs.iterator();

            while(irecs.hasNext()) {
                SemaphoreSensor record = (SemaphoreSensor) irecs.next();
                Tuple2 tp2 = new Tuple2();
                tp2.setField(record.getSemaphoreID(), 0);
                tp2.setField(record.getMeanSpeed(), 1 );

                out.collect(tp2);
            }

        }
    }}
