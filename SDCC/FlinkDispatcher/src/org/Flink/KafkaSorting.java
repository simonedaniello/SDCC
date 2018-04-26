package org.Flink;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Properties;

public class KafkaSorting {

    // The topic we want the source data to come from
    public static final String MAIN_TOPIC = "test2";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "message-sorter");

        // Change this to YOUR kafka server or just set the KAFKA ENV variable!
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA"));
        // Change this to YOUR zookeeper server or just set the ZOOKEEPER ENV variable!
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, System.getenv("ZOOKEEPER"));

        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Reset to the earliest position so we can reprocess the messages for the demo
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // load a simple json serializer
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        // load a simple json deserializer
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        // use the simple json serializer and deserialzer we just made and load a Serde for streaming data
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        // Setup a string Serde for the key portion of the messages
        final Serde<String> stringSerde = Serdes.String();

        // Setup a builder for the streams
        KStreamBuilder builder = new KStreamBuilder();

        // Just so we know everything is working.. let's report that we are about to start working
        System.out.println("Starting Sorting Job");

        // Lets start a source and set it to deserialize the key as a String and the value as a JSON object.
        //
        // We know that all of our messages are keyed with a string and the payload is a string representation of
        //   a JSON object. By assigning our jsonSerde to the value, Kafka, will automatically deserialize the value
        //   as a JSON object so we can navigate the data in the JSON tree without any extra work.
        KStream<String, JsonNode> source = builder.stream(stringSerde, jsonSerde, MAIN_TOPIC);


        // Predicates... these are fun to figure out...
        // Think of Predicates as simple matchers that know how to traverse the data and return a boolean
        //
        // At this point we want to setup a few Predicates to use for sorting

        // This Predicate is checking to see if the Hostname is mockedhost1.mikeheijmans.com
        Predicate<String, JsonNode> isFirstHost = (k, v) ->
                v.path("host")                           // Read the value of "host" from the json object
                        .asText()                                // Turn it into a String -> This is a gotcha for newbies like me...
                        .equals("mockedhost1.mikeheijmans.com"); // Return a boolean of what we want

        // This Predicate is checking if the metrics object is of the type "Counter"
        Predicate<String, JsonNode> isCounter = (k, v) ->
                v.path("type")       // Read the value of "type" from the json object
                        .asText()            // Turn it into a String -> This is a gotcha for newbies like me...
                        .equals("counter");  // Return a boolean of what we want

        // This Predicate is checking if the metrics object is of the type "Gauge"
        Predicate<String, JsonNode> isGauge = (k, v) ->
                v.path("type")       // Read the value of "type" from the json object
                        .asText()            // Turn it into a String -> This is a gotcha for newbies like me...
                        .equals("gauge");    // Return a boolean of what we want


        // Simple filtering processor
        // We setup another stream called firstHost to receive messages that match the filter with the
        //   the "isFirstHost" predicate
        KStream<String, JsonNode> firstHost = source.filter(isFirstHost);

        // Take all messages coming into the firstHost steam and sink them to the 'firsthost' topic
        //   we are re-serializing the object back into a string using our jsonSerde on the value field.
        firstHost.to(stringSerde, jsonSerde, "firsthost");

        // Branching processor
        // Here we setup a branching filter to branch gauges and counters into seperate streams
        //   Everything that matches the first Predicate is put into index 0 of the array and
        //   everything that matches the second Predicate is put into the index of 1 of the array
        //   and so on for multiple branching Predicates
        KStream<String, JsonNode>[] metricTypes = source.branch(isGauge, isCounter);

        // Take all messages coming into the metricTypes streams and sink them to the proper topics
        // In this case, metricTypes[0] is filled with gauge events and metricTypes[1] is filled with counter
        //   events. We sink them to appropriately named topics. And again, we use the stringSerde for the key
        //   and the jsonSerde for the value so that the json object gets turned back to a string properly on egress.
        metricTypes[0].to(stringSerde, jsonSerde, "gauges");
        metricTypes[1].to(stringSerde, jsonSerde, "counters");

        // Now lets use that builder and the props we set to setup a streams object
        KafkaStreams streams = new KafkaStreams(builder, props);

        // Start the streaming processor
        streams.start();

        // Gracefully shutdown on an interrupt
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
