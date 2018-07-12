package org.Flink;


import Model.SemaphoreJsonReader;
import Model.SemaphoreSensor;
import algorithms.PSquared;
import com.google.gson.Gson;
import Model.FlinkResult;
import Model.Message;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class NewMedianKafkaSender {


    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final Logger log = LoggerFactory.getLogger(org.Flink.WindowTrafficData.class);


    public void calculateMedian() throws Exception {

//        INPUT_KAFKA_TOPIC = "semaphoresensor";
//        TIME_WINDOW = 10;
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//        properties.setProperty("zookeeper.connect", "localhost:2181");
//        properties.setProperty("group.id", INPUT_KAFKA_TOPIC);


        Properties properties = new Properties();
        String filename = "org/Flink/consumer.props";
        InputStream input = NewAverageKafkaSender.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("\n\n\n\n\nSorry, unable to find " + filename);
            return;
        }
        properties.load(input);


        TIME_WINDOW = 10;
        INPUT_KAFKA_TOPIC = properties.getProperty("INPUT_KAFKA_TOPIC");
        String flinkDispatcherID = properties.getProperty("flinkDispatcherID");
        String topicname = properties.getProperty("topic_name");
        String BROKER_NAME = properties.getProperty("broker_name");



        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011(INPUT_KAFKA_TOPIC, new SimpleStringSchema(), properties));

/*
        System.out.println("got sources");
*/
        // DataStream<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> streamTuples = stream.flatMap(new semaphoreAssigner());
        DataStream<Tuple2<String, Double>> streamTuples = stream.flatMap(new SemaphoreJson2Tuple());

        streamTuples.print();
        DataStream<Tuple3<String, Double, Integer>> averageSpeedStream = streamTuples
                .keyBy(new int[]{0})
                .timeWindow(Time.seconds((long)TIME_WINDOW))
                .aggregate((AggregateFunction<Tuple2<String, Double>, Tuple3 <String, PSquared, Integer>, Tuple3<String, Double, Integer>>) new MedianAggregate());


        //write to another kafka topic
        averageSpeedStream.addSink(new FlinkKafkaProducer011<>(BROKER_NAME, topicname, (SerializationSchema<Tuple3<String, Double, Integer>>) stringDoubleTuple3 -> {
            Gson gson = new Gson();
            String key = stringDoubleTuple3.f0;
            double value = stringDoubleTuple3.f1;
            int count = stringDoubleTuple3.f2;
            FlinkResult flinkResult = new FlinkResult(key, value, count);
            Message m = new Message(flinkDispatcherID, 70215);
            m.setFlinkResult(flinkResult);
            String result = gson.toJson(m);
            return result.getBytes();
        }));

        averageSpeedStream.print();
        env.execute("Query 2");

    }





/*
    Following Class executes flink for tuples generated by semaphores' sensor.
 */

    private static class MedianAggregate implements AggregateFunction<Tuple2<String, Double>, Tuple3<String, PSquared, Integer>, Tuple3<String, Double, Integer>> {

        @Override
        public Tuple3<String, PSquared, Integer> createAccumulator() {
            return new Tuple3<>("initializer", new PSquared(0.5f), 0);
        }

        @Override
        public Tuple3<String,PSquared,Integer> add(Tuple2<String, Double> value, Tuple3<String,PSquared,Integer> accumulator) {
            accumulator.f1.accept( value.f1.floatValue());
            return new Tuple3<>(value.f0, accumulator.f1, accumulator.f2+1);
        }

        @Override
        public Tuple3<String, Double, Integer> getResult(Tuple3<String,PSquared,Integer> accumulator) {
//            System.out.println("Median is: ");
            return new Tuple3<>(accumulator.f0, (double) accumulator.f1.getPValue(), accumulator.f2);        }

        @Override
        public Tuple3<String,PSquared,Integer> merge(Tuple3<String,PSquared,Integer> a, Tuple3<String,PSquared,Integer> b) {
            return null;
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
