package myapps;

import myapps.config.KafkaStreamConfig;
import myapps.partitioner.CustomPartitioner;
import myapps.transformer.DynamicProcessor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Pattern;

public class DynamicPartitionTopologyForwarder {
    private static final Logger logger = LoggerFactory.getLogger(DynamicPartitionTopologyForwarder.class);
    public static void main(String[] args) {
        KafkaStreamConfig config = new KafkaStreamConfig(
                new String[]{System.getenv("KAFKA_BROKER")},
                System.getenv("KAFKA_SOURCE_TOPICS").split(","),
                System.getenv("KAFKA_SINK_TOPICS").split(",")
        );

        StreamsBuilder builder = new StreamsBuilder();
        Topology topology = builder.build();

        // NOTE: Source 설정
        topology.addSource("Source", Pattern.compile("streams\\-.*input"));
//        for (String sourceTopic : config.getSourceTopics()) {
//
//        }

        // TODO: 이것도 동적으로 설정할 수 있지 않을까?
        topology.addProcessor("Process", DynamicProcessor::new, "Source");

        // NOTE: Sink 설정
        for (String sinkTopic : config.getSinkTopics()) {
            topology.addSink(sinkTopic, sinkTopic, Serdes.String().serializer(), Serdes.String().serializer(), "Process");
        }

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "message-broker11");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", config.getBrokers()));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // TODO: 이것도 동적으로 설정할 수 있지 않을까?
        props.put(StreamsConfig.producerPrefix(ProducerConfig.PARTITIONER_CLASS_CONFIG), CustomPartitioner.class.getName());

        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
