package com.ecubelabs

import com.ecubelabs.configs.EnvConfig
import com.ecubelabs.configs.KafkaStreamsConfig
import com.ecubelabs.libs.k8s.HealthCheckApp
import com.ecubelabs.processor.ReplicationProcessor
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

fun main() {
    HealthCheckApp.execute {
        val config = initKafkaStreamsConfig(EnvConfig())
        val streams = KafkaStreams(initTopology(config), initProperties(config))
        streams.start()
        Runtime.getRuntime().addShutdownHook(Thread { streams.close() })
    }
}

fun initKafkaStreamsConfig(envConfig: EnvConfig): KafkaStreamsConfig {
    return KafkaStreamsConfig(
            "kafka-streams-broadcaster",
            envConfig.kafkaBrokers,
            arrayOf("debezium.ben.ddd_event"),
            fetchClientIds(envConfig).map { "partitioned.ben-$it.domain_event" }.toTypedArray(),
    )
}

// TODO: 별도 파일로 분리?
//fun fetchClientIds(envConfig: EnvConfig): Array<String> {
//    val httpClient: CloseableHttpClient = HttpClients.createDefault()
//    val request = HttpGet("${envConfig.apiHost}/topics?keyword=partitioned.ben")
//    request.addHeader("Authorization", "Bearer ${envConfig.accessToken}")
//    request.addHeader("x-city-id", "1")
//    request.addHeader("x-region-id", "1")
//    try {
//        httpClient.execute(request).use { response ->
//            val statusCode = response.statusLine.statusCode
//
//
//            BufferedReader(InputStreamReader(response.entity.content)).use { reader ->
//                val responseString = reader.readLine()
//                val objectMapper = ObjectMapper()
//                val jsonNode: JsonNode = objectMapper.readTree(responseString)
////                return jsonNode["data"].map { it["id"].asText() }.toTypedArray()
//
//                return arrayOf("1337022124")
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        throw e
//    }
//}
// TODO: 빠른 테스트를 위해서 생략
fun fetchClientIds(envConfig: EnvConfig): Array<String> {
    return Array(20) { i -> (i + 1).toString() }
}

fun initProperties(config: KafkaStreamsConfig): Properties {
    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = config.name
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = config.brokers.joinToString(",")
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
    props[StreamsConfig.consumerPrefix("auto.offset.reset")] = "latest" // NOTE: 최신 offset 부터 구독한다.
    props[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE_V2 // NOTE: EXACTLY_ONCE 설정 (트랜잭션 활성화)
    return props
}

fun initTopology(config: KafkaStreamsConfig): Topology {
//    checkTopicsExist(config)

    val builder = StreamsBuilder()
    val topology = builder.build()
    // NOTE: Source 설정
    for (sourceTopic in config.sourceTopics) {
        topology.addSource("Source", Pattern.compile(sourceTopic))
    }

    topology.addProcessor("Process", ProcessorSupplier { ReplicationProcessor() }, "Source")

    // NOTE: Sink 설정
    for (sinkTopic in config.sinkTopics) {
        topology.addSink<String, String>(sinkTopic, sinkTopic, Serdes.String().serializer(), Serdes.String().serializer(), "Process")
    }

    return topology;
}

fun checkTopicsExist(config: KafkaStreamsConfig) {
    val props = Properties()
    props["bootstrap.servers"] = config.brokers.joinToString(",")
    val adminClient = AdminClient.create(props)

    try {
        val existingTopics = adminClient.listTopics().names().get()
        val missingTopics = mutableListOf<String>()
        config.sourceTopics.forEach { topic ->
            if (!existingTopics.contains(topic)) {
                missingTopics.add(topic)
            }
        }
        config.sinkTopics.forEach { topic ->
            if (!existingTopics.contains(topic)) {
                missingTopics.add(topic)
            }
        }

        if (missingTopics.isNotEmpty()) {
            throw RuntimeException("Missing required Kafka topics: ${missingTopics.joinToString(", ")}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    } finally {
        adminClient.close()
    }
}
