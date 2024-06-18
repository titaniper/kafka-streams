package myapps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

public class DynamicTopicForwarder {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dynamic-forwarder-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream("streams\\-.*input");
        // 처리 로직 추가 (예: 동적으로 to 지정)
        sourceStream.foreach((key, value) -> {
            String targetTopic;
            if (value.contains("condition1")) {
                targetTopic = "destination-topic-1";
            } else if (value.contains("condition2")) {
                targetTopic = "destination-topic-2";
            } else {
                targetTopic = "default-destination-topic";
            }
            sourceStream.to(targetTopic, Produced.with(Serdes.String(), Serdes.String()));
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
