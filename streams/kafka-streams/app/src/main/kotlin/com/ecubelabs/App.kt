package com.ecubelabs

import com.ecubelabs.configs.KafkaStreamAppConfig
import com.ecubelabs.streams.debezium.partitioners.DebeziumPartitioner
import com.ecubelabs.streams.debezium.processors.DebeziumReproducingMessageProcessor
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import java.util.*
import java.util.regex.Pattern

fun main() {
    val config = initConfig()
    val streams = KafkaStreams(initTopology(config), initProperties(config))
    streams.start()
    Runtime.getRuntime().addShutdownHook(Thread { streams.close() })
}

fun initConfig(): KafkaStreamAppConfig {
    return KafkaStreamAppConfig(
            System.getenv("APP_NAME"),
            arrayOf<String>(System.getenv("KAFKA_BROKER")),
            System.getenv("KAFKA_SOURCE_TOPICS").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(),
            System.getenv("KAFKA_SINK_TOPICS").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    )
}

fun initProperties(config: KafkaStreamAppConfig): Properties {
    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = config.name
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = config.brokers.joinToString(",")
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
    props[StreamsConfig.consumerPrefix("auto.offset.reset")] = "latest"
    // TODO: 이것도 동적으로 설정할 수 있지 않을까?
    props[StreamsConfig.producerPrefix(ProducerConfig.PARTITIONER_CLASS_CONFIG)] = DebeziumPartitioner::class.java.getName()
    return props
}

fun initTopology(config: KafkaStreamAppConfig): Topology {
    val builder = StreamsBuilder()
    val topology = builder.build()
    // NOTE: Source 설정
    for (sourceTopic in config.getSourceTopics()) {
        topology.addSource("Source", Pattern.compile(sourceTopic))
    }

    // TODO: 이것도 동적으로 설정할 수 있지 않을까?
    topology.addProcessor("Process", ProcessorSupplier { DebeziumReproducingMessageProcessor() }, "Source")

    // NOTE: Sink 설정
    for (sinkTopic in config.getSinkTopics()) {
        topology.addSink<String, String>(sinkTopic, sinkTopic, Serdes.String().serializer(), Serdes.String().serializer(), "Process")
    }

    return topology;
}