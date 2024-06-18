package myapps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Pattern;

public class DynamicTopicProducer {
    private static final Logger logger = LoggerFactory.getLogger(DynamicTopicProducer.class);

    public static void main(String[] args) {
        logger.info("Start DynamicTopicProducer");

        // Kafka Streams 설정
        Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "dynamic-topic-producer3");
        streamsProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsProps.put(StreamsConfig.consumerPrefix("auto.offset.reset"), "latest");

        StreamsBuilder builder = new StreamsBuilder();
//        KStream<String, String> sourceStream = builder.stream("streams\\-.*input");
//        KStream<String, String> sourceStream = builder.stream("streams\\-.*input");
//        KStream<String, String> sourceStream = builder.stream("streams-plaintext-input");
        // 정규식 가능
        KStream<String, String> sourceStream = builder.stream(Pattern.compile("streams\\-.*input"));
        // account 파티션 지정할 수 있으면, haulla repo에 넣고 그게 아니면

        // Kafka Producer 설정
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        // 메시지를 수신하고 분석하여 동적으로 토픽 선택 후 발행
        sourceStream.foreach((key, value) -> {
            String targetTopic = selectTopicBasedOnMessageContent(value);
            ProducerRecord<String, String> record = new ProducerRecord<>(targetTopic, key, value);
            producer.send(record);
        });

//        sourceStream.to();

        KafkaStreams streams = new KafkaStreams(builder.build(), streamsProps);
        streams.start();

        // Kafka Streams 종료 시 Kafka Producer도 닫기
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            producer.close();
        }));
    }

    private static String selectTopicBasedOnMessageContent(String message) {
        // 메시지 내용을 분석하여 토픽을 선택하는 로직 구현
        if (message.contains("condition1")) {
            return "topic1";
        } else if (message.contains("condition2")) {
            return "topic2";
        } else {
            return "default-topic";
        }
    }
}
