package com.ecubelabs

import com.ecubelabs.configs.ConfigLoader
import com.ecubelabs.configs.KafkaStreamAppConfig
import com.ecubelabs.streams.debezium.partitioners.DebeziumPartitioner
import com.ecubelabs.streams.debezium.processors.DebeziumReproducingMessageProcessor
import com.ecubelabs.utils.k8s.HealthCheckApp
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
    HealthCheckApp.execute {
        val config = initConfig()
        val streams = KafkaStreams(initTopology(config), initProperties(config))
        streams.start()
        Runtime.getRuntime().addShutdownHook(Thread {
            streams.close()
        })
    }
}

fun initConfig(): KafkaStreamAppConfig {
    val configLoader = ConfigLoader();
    val topics = configLoader.load()
    return KafkaStreamAppConfig(
            System.getenv("APP_NAME"),
            arrayOf<String>(System.getenv("KAFKA_BROKER")),
            System.getenv("KAFKA_SOURCE_TOPICS").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(),
            topics
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
//    props[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE_V2 // NOTE: EXACTLY_ONCE 설정 (트랜잭션 활성화)
    return props
}

fun initTopology(config: KafkaStreamAppConfig): Topology {
    val builder = StreamsBuilder()
    val input = builder.stream<String, String>(Pattern.compile("debezium.ben.ddd_event"))

    // NOTE: Sink 설정
    for (sinkTopic in config.getSinkTopics()) {
        input.to(sinkTopic)
    }

    return builder.build()
}
