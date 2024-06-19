package com.ecubelabs.streams.debezium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumValuePayloadAfter {
    @JsonProperty("id")
    public long id;

    @JsonProperty("type")
    public String type;

    @JsonProperty("occurredAt")
    public long occurredAt;

    @JsonProperty("txId")
    public String txId;

    @JsonProperty("createdAt")
    public long createdAt;

    @JsonProperty("updatedAt")
    public long updatedAt;

    @JsonProperty("data")
    public String data;

    @JsonProperty("actorId")
    public String actorId;

    @JsonProperty("metadata")
    private String metadata;

    public Optional<DebeziumValuePayloadAfterMetadata> getMetadata() throws JsonProcessingException {
        return  Optional.ofNullable(this.metadata == null ? null : new ObjectMapper().readValue(this.metadata, DebeziumValuePayloadAfterMetadata.class));
    }
}

