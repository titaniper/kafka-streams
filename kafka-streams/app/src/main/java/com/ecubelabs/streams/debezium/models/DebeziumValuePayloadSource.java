package com.ecubelabs.streams.debezium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumValuePayloadSource {
    @JsonProperty("version")
    public String version;

    @JsonProperty("connector")
    public String connector;

    @JsonProperty("name")
    public String name;

    @JsonProperty("ts_ms")
    public long tsMs;

    @JsonProperty("snapshot")
    public String snapshot;

    @JsonProperty("db")
    public String db;

    @JsonProperty("sequence")
    public String sequence;

    @JsonProperty("table")
    public String table;

    @JsonProperty("server_id")
    public long serverId;

    @JsonProperty("gtid")
    public String gtid;

    @JsonProperty("file")
    public String file;

    @JsonProperty("pos")
    public long pos;

    @JsonProperty("row")
    public int row;

    @JsonProperty("thread")
    public long thread;

    @JsonProperty("query")
    public String query;
}