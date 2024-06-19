package com.ecubelabs.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MessagePartitioner implements Partitioner {
    private static final Logger logger = LoggerFactory.getLogger(MessagePartitioner.class);

    public void configure(Map<String, ?> configs) {
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        String keyString = new String(keyBytes, StandardCharsets.UTF_8);
        String valueString = new String(valueBytes, StandardCharsets.UTF_8);
        logger.info(String.format("\"!!!partition\": %s", keyString));

        if (keyString.equals("key1")) {
            return 0;
        } else {
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            int numPartitions = partitions.size();
            int result = (key.hashCode() & Integer.MAX_VALUE) % numPartitions;
            return result;
        }
    }

    @Override
    public void close() {
    }
}
