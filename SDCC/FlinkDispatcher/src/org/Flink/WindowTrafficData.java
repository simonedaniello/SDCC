package org.Flink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowTrafficData {
    private static String INPUT_KAFKA_TOPIC = null;
    private static int TIME_WINDOW = 0;
    private static final String topicname = "monitorer";
    private static final Logger log = LoggerFactory.getLogger(WindowTrafficData.class);

    public WindowTrafficData() {
    }

    public static void main(String[] args) {

       try {
            Thread thread1 = new Thread(() -> {
                NewAverageKafkaSender avg = new NewAverageKafkaSender();
                try {
                    avg.calculateAvg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread thread2 = new Thread(() -> {
                NewMedianKafkaSender qkafka = new NewMedianKafkaSender();
                try {
                    qkafka.calculateMedian();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
//        INPUT_KAFKA_TOPIC = "test";
//        TIME_WINDOW = 20;
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//        properties.setProperty("zookeeper.connect", "localhost:2181");
//        properties.setProperty("group.id", "test");
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        DataStreamSource<String> stream = env.addSource(new FlinkKafkaConsumer011("test", new SimpleStringSchema(), properties));
//
//
//        System.out.println("got sources");
//        DataStream<Tuple6<String, String, String, String, String, Double>> streamTuples = stream.flatMap(new WindowTrafficData.IoTJson2Tuple());
//        streamTuples.print();
//        DataStream<Tuple2<String, Double>> averageSpeedStream = streamTuples.keyBy(new int[]{0}).timeWindow(Time.seconds((long)TIME_WINDOW),Time.seconds((long)TIME_WINDOW)).apply(new querySolver()).setParallelism(1);
//        averageSpeedStream.addSink(new FlinkKafkaProducer011<>("localhost:9092", topicname, (SerializationSchema<Tuple2<String, Double>>) stringDoubleTuple2 -> {
//            Gson gson = new Gson();
//            String key = stringDoubleTuple2.f0;
//            double value = stringDoubleTuple2.f1;
//            FlinkResult flinkResult = new FlinkResult(key, value);
//            Message m = new Message("flinkDispatcher", 701);
//            m.setFlinkResult(flinkResult);
//            String result = gson.toJson(m);
//            return result.getBytes();
//        }));
//        averageSpeedStream.print();
//        env.execute("Window Traffic Data");
//
//    }
//
//    public static class querySolver implements WindowFunction<Tuple6<String, String, String, String, String, Double>, Tuple2<String, Double>, Tuple, TimeWindow> {
//        public querySolver() {
//        }
//
//        @Override
//        public void apply(Tuple key, TimeWindow window, Iterable<Tuple6<String, String, String, String, String, Double>> records, Collector<Tuple2<String, Double>> out) throws Exception {
//            int count = 0;
//         //   double speedAccumulator = 0.0D;
//            QuantileEstimator qe = new QuantileEstimator(5); //estimates quantiles
//            Welford welford = new Welford(); //calculate mean value
//            Iterator var8 = records.iterator();
//
//            while(var8.hasNext()) {
//                Tuple6<String, String, String, String, String, Double> record = (Tuple6)var8.next();
//                double speed = (Double)record.getField(5);
//              //  System.out.println("speed is: " + speed);
//       //         ++count;
//       //         speedAccumulator += speed;
//                welford.addElement(speed);
//                qe.add(speed);
//                System.out.println("accumuling speed");
//            }
//
//       //     double averageSpeed = speedAccumulator / (double)count;
//            double averageSpeed = welford.getCurrent_mean();
//            System.out.println("medium speed is:");
//            out.collect(new Tuple2(WindowTrafficData.INPUT_KAFKA_TOPIC, averageSpeed));
//            List<Double> quantiles = qe.getQuantiles();
//            System.out.println(qe.getQuantiles());
//            System.out.println("median of speed is:");
//            out.collect(new Tuple2(WindowTrafficData.INPUT_KAFKA_TOPIC, quantiles.get(2)));
//            qe.clear();
//            welford.resetIndexes();
//        }
//    }
//
//    public static class IoTJson2Tuple implements FlatMapFunction<String, Tuple6<String, String, String, String, String, Double>> {
//        public IoTJson2Tuple() {
//        }
//
//        public void flatMap(String jsonString, Collector<Tuple6<String, String, String, String, String, Double>> out) throws Exception {
//            ArrayList<IoTData> recs = GPSJsonReader.getIoTDatas(jsonString);
//            Iterator irecs = recs.iterator();
//
//            while(irecs.hasNext()) {
//                IoTData record = (IoTData)irecs.next();
//                Tuple6<String, String, String, String, String, Double> tp9 = new Tuple6();
//                tp9.setField(WindowTrafficData.INPUT_KAFKA_TOPIC, 0);
//                tp9.setField(record.getTimestamp(), 1);
//                tp9.setField(record.getVehicleId(), 2);
//                tp9.setField(record.getLatitude(), 3);
//                tp9.setField(record.getLongitude(), 4);
//                tp9.setField(record.getSpeed(), 5);
//                out.collect(tp9);
//            }
//
//        }
//    }

}
