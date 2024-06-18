package myapps;

import myapps.partitioner.CustomPartitioner;
import myapps.transformer.DynamicProcessor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

public class DynamicPartitionTopologyForwarder {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dynamic-partition-forwarder-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.producerPrefix(ProducerConfig.PARTITIONER_CLASS_CONFIG), CustomPartitioner.class.getName());

        StreamsBuilder builder = new StreamsBuilder();
        Topology topology = builder.build();
        topology.addSource("Source", "streams-plaintext-input");
        topology.addProcessor("Process", DynamicProcessor::new, "Source");
        // 싱크 노드 추가
        topology.addSink("dynamic-partition-forwarder-app-typeA", "dynamic-partition-forwarder-app-typeA", Serdes.String().serializer(), Serdes.String().serializer(), "Process");
        topology.addSink("dynamic-partition-forwarder-app-typeB", "dynamic-partition-forwarder-app-typeB", Serdes.String().serializer(), Serdes.String().serializer(), "Process");
        topology.addSink("dynamic-partition-forwarder-app-typeC", "dynamic-partition-forwarder-app-typeC", Serdes.String().serializer(), Serdes.String().serializer(), "Process");

        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
