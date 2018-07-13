package org.Flink;

import Model.GPSJsonReader;
import Model.IoTData;
import algorithms.Harvesine;
import com.google.gson.Gson;
import Model.FlinkResult;
import Model.Message;
import entities.Semaphore;
import entities.system.Printer;
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
import java.util.Arrays;
import java.util.Properties;

public class Query3Solver {


    private static int TIME_WINDOW;
    private static final Logger log = LoggerFactory.getLogger(org.Flink.WindowTrafficData.class);
    String[] latitudes;
    String[] longitudes;


    public void calculateMedian() throws Exception {

        Properties properties = new Properties();
        String filename = "org/Flink/consumer.props";
        InputStream input = NewAverageKafkaSender.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("\n\n\n\n\nSorry, unable to find " + filename);
            return;
        }
        properties.load(input);

        Properties semaphores = new Properties();
        String semaphoresConfig = "org/Flink/semaphoresConfig.props";
        InputStream semaphoreInput = NewAverageKafkaSender.class.getClassLoader().getResourceAsStream(semaphoresConfig);
        if(input==null){
            System.out.println("\n\nSorry, unable to find " + filename);
            return;
        }
        semaphores.load(semaphoreInput);

        longitudes = semaphores.getProperty("longitude").split(",");
        latitudes = semaphores.getProperty("latitude").split(",");


        double[] doubleLatitudes = Arrays.stream(latitudes)
                .mapToDouble(Double::parseDouble)
                .toArray();

        double[] doubleLongitudes = Arrays.stream(longitudes)
                .mapToDouble(Double::parseDouble)
                .toArray();


        TIME_WINDOW = Integer.valueOf(properties.getProperty("QUERY3_TIME_WINDOW"));
        String flinkDispatcherID = properties.getProperty("flinkDispatcherID");
        String outputTopic = properties.getProperty("query_3_output_topic");
        String INPUT_KAFKA_TOPIC = properties.getProperty("query_3_input_topic");
        String BROKER_NAME = properties.getProperty("broker_name");




        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011(INPUT_KAFKA_TOPIC, new SimpleStringSchema(), properties));


        // DataStream<Tuple11<String, String, String, String, String, Int, Double, Double ,Boolean,Boolean,Boolean>> streamTuples = stream.flatMap(new semaphoreAssigner());
        DataStream<Tuple2<String, Double>> streamTuples = stream.flatMap(new semaphoreAssigner(doubleLatitudes,doubleLongitudes));
        streamTuples.print();

        DataStream<Tuple3<String, Double,Integer>> result = streamTuples
                .keyBy(new int[]{0})
                .timeWindow(Time.seconds((long)TIME_WINDOW))
                .aggregate(new mobileSensorAggregate());

        result.addSink(new FlinkKafkaProducer011<>(BROKER_NAME, outputTopic, (SerializationSchema<Tuple3<String, Double, Integer>>) stringDoubleTuple3 -> {
            Gson gson = new Gson();
            String key = stringDoubleTuple3.f0;
            double value = stringDoubleTuple3.f1;
            int count = stringDoubleTuple3.f2;
            FlinkResult flinkResult = new FlinkResult(key, value, count);
            Message m = new Message(flinkDispatcherID, 70315);
            m.setFlinkResult(flinkResult);
            String results = gson.toJson(m);
            return results.getBytes();
        }));

        env.execute("Query 3");

    }


    //Read Mobile sensors' Json and create Tuples
    public static class semaphoreAssigner implements FlatMapFunction<String, Tuple2<String,Double>> {

        private double[] latitudes;
        private double[] longitudes;
        private double semaphoreActionRange = 0.02; //range of action of semaphore in kilometres
        private Harvesine harvesine = new Harvesine();



        public semaphoreAssigner(double[] latitudes, double[] longitudes) {
            this.latitudes = latitudes;
            this.longitudes = longitudes;
        }

        @Override
        public void flatMap(String jsonString, Collector< Tuple2<String, Double>> out) {


            IoTData car = GPSJsonReader.getIoTData(jsonString);

            boolean found = false;
            int i;
            double threshold = semaphoreActionRange;  //for each new car, reset threshold to action range
            String semaphoreID = "";                  //String that will contain ID of associated semaphore

            assert car != null;
            double carLat = Double.parseDouble(car.getLatitude());
            double carLon = Double.parseDouble(car.getLongitude());

/*            System.out.println("latitudine macchina" + carLat);
            System.out.println("longitudine macchina" + carLon);*/

            for (i=0; i< latitudes.length ; i++){
                double semaphoreLat = latitudes[i];
                double semaphoreLon = longitudes[i];

                Double dist = harvesine.calculateApproximatedDistanceInKilometer(carLat,carLon,semaphoreLat,semaphoreLon);
/*
                Printer.getInstance().print(dist.toString(),"yellow");
*/
                if (dist <= threshold){
                    found = true;
                    threshold = dist;
                    semaphoreID = org.apache.commons.codec.digest.DigestUtils.sha256Hex(String.valueOf(semaphoreLat) + String.valueOf(semaphoreLon));
                    }

                }

                if (found){

                    Tuple2<String,Double> tp2 = new Tuple2<>();
                    tp2.setField(semaphoreID,0);
                    tp2.setField(car.getSpeed(),1);
                    out.collect(tp2);

                    System.out.println(out);

            }
        }
    }

    private static class mobileSensorAggregate implements
            AggregateFunction<Tuple2<String, Double>, Tuple3<String,Double, Double>, Tuple3<String, Double, Integer>> {

        @Override
        public Tuple3<String, Double, Double> createAccumulator() {
            return new Tuple3<>("initializer", 0.0, 0.0);
        }

        @Override
        public Tuple3<String,Double, Double> add(Tuple2<String, Double> value, Tuple3 <String, Double, Double> accumulator) {

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


}

