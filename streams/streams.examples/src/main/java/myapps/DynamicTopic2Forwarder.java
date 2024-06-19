package myapps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DynamicTopic2Forwarder {
    private static final Logger logger = LoggerFactory.getLogger(DynamicTopic2Forwarder.class);

    public static void main(String[] args) {
        logger.info("Start DynamicTopic2Forwarder");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dynamic-topic-producer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream("streams\\-.*input");

        // 메시지 내용을 분석하여 동적으로 토픽을 선택
        Predicate<String, String> isCondition1 = (key, value) -> value.contains("condition1");
        Predicate<String, String> isCondition2 = (key, value) -> value.contains("condition2");

        KStream<String, String>[] branches = sourceStream.branch(isCondition1, isCondition2, (key, value) -> true);
        branches[0].to("topic1", Produced.with(Serdes.String(), Serdes.String()));
        branches[1].to("topic2", Produced.with(Serdes.String(), Serdes.String()));
        branches[2].to("default-topic", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}