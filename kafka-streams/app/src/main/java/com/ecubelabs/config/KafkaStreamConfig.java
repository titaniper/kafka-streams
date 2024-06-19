package com.ecubelabs.config;

public class KafkaStreamConfig {
    private String[] brokers;

    private String[] sourceTopics;

    private String[] sinkTopics;

    public KafkaStreamConfig(String[] brokers, String[] sourceTopics, String[] sinkTopics) {
        this.brokers = brokers;
        this.sourceTopics = sourceTopics;
        this.sinkTopics = sinkTopics;
    }

    public String[] getBrokers() {
        return brokers;
    }

    public String[] getSinkTopics() {
        return sinkTopics;
    }

    public String[] getSourceTopics() {
        return sourceTopics;
    }
}
