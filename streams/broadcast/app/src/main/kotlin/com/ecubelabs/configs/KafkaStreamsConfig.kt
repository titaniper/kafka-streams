package com.ecubelabs.configs

class KafkaStreamsConfig(
        val name: String,
        val brokers: Array<String>,
        val sourceTopics: Array<String>,
        val sinkTopics: Array<String>
)