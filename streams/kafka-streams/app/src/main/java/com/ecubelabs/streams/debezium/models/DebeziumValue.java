package com.ecubelabs.streams.debezium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumValue {
    @JsonProperty("schema")
    public DebeziumValueSchema schema;

    @JsonProperty("payload")
    public DebeziumValuePayload payload;
}