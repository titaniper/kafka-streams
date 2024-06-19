package com.ecubelabs.streams.debezium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumValueSchema {
    @JsonProperty("type")
    public String type;

    @JsonProperty("optional")
    public boolean optional;

    @JsonProperty("name")
    public String name;

    @JsonProperty("version")
    public int version;
}