package myapps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DynamicBranchForwarder {
    private static final Logger logger = LoggerFactory.getLogger(DynamicBranchForwarder.class);
    public static void main(String[] args) {
        logger.info("start DynamicBranchForwarder");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dynamic-forwarder-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream("streams\\-.*input");
        KStream<String, String>[] branches = sourceStream.branch(
                (key, value) -> value.contains("condition1"),
                (key, value) -> value.contains("condition2"),
                (key, value) -> true
        );
        branches[0].to("destination-topic-1", Produced.with(Serdes.String(), Serdes.String()));
        branches[1].to("destination-topic-2", Produced.with(Serdes.String(), Serdes.String()));
        branches[2].to("default-destination-topic", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
