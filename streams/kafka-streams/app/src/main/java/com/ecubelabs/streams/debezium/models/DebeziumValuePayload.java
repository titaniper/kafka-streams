package com.ecubelabs.streams.debezium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumValuePayload {
    @JsonProperty("after")
    public DebeziumValuePayloadAfter after;

    @JsonProperty("source")
    public DebeziumValuePayloadSource source;
}
