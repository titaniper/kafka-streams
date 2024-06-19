package com.ecubelabs.streams.debezium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumValuePayloadAfterMetadata {
    @JsonProperty("channel")
    public String channel;

    @JsonProperty("from")
    public String from;

    @JsonProperty("to")
    public String to;

    @JsonProperty("partition")
    public Integer partition;

    @JsonProperty("enabled")
    public boolean enabled;
}
