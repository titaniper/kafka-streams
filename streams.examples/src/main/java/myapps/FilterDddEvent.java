/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package myapps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text;
 * the code split each text line in string into words and then write back into a sink topic "streams-linesplit-output" where
 * each record represents a single word.
 */
public class FilterDddEvent {
    private static final Logger logger = LoggerFactory.getLogger(FilterDddEvent.class);

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-filterevent");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        builder.<String, String>stream(Pattern.compile("streams\\-.*input"))
               .flatMapValues(new ValueMapper<String, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(String value) {
                        return Arrays.asList(value.split("\\W+"));
                    }
                })
                .map(new KeyValueMapper<String, String, KeyValue<?, ?>>() {
                    @Override
                    public KeyValue<?, ?> apply(String key, String value) {
                        String topicName = "";
                        try {
                            JsonNode root = objectMapper.readTree(key);
                            JsonNode schema = root.get("schema");
                            topicName = schema.get("name").asText();
                            logger.info(topicName);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        HashMap payload = new HashMap<>();
                        payload.put("key", key);
                        payload.put("value", value);
                        return KeyValue.pair(topicName, payload);
                    }
                })
                .filter((key, value) -> {
                    logger.info(String.format("key=%s, value=%s", key, value));
                    return ((String) key).startsWith("debezium");
                })
                .to("streams.*");
//                .foreach((topic, value) -> {
//                    // Determine the original topic name
//                    String originalTopic = "stream"; // Replace with the logic to get the original topic name
//                    String outputTopic = originalTopic + ".out";
////                    builder.stream(originalTopic).to(outputTopic);
//                });


        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
