package com.ecubelabs.processor

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.kafka.streams.processor.AbstractProcessor

class ReplicationProcessor : AbstractProcessor<String?, String?>() {
    override fun process(key: String?, value: String?) {
        val objectMapper = ObjectMapper()
        try {
            val jsonNode: JsonNode = objectMapper.readTree(value)
            val afterNode: ObjectNode = jsonNode.get("payload").get("after") as ObjectNode
            val type = afterNode.get("type").asText()
            if (type.equals("KillEvent")) {
                throw Exception("KillEvent is occurred.")
            }

            val newType = when (type) {
                "AccountUpdatedEvent" -> "ReplicateAccountCommand"
                "IndividualServiceUpdatedEvent" -> "ReplicateServiceCommand"
                "BiddingUpdatedEvent" -> "ReplicateBiddingCommand"
                else -> null
            }

            // NOTE: 필요한 이벤트가 아니면 복제하지 않는다.
            if (newType != null) {
                afterNode.put("type", newType)
                val newValue = jsonNode.toString()
                context().forward(key, newValue)
            }
        } catch (e: JsonProcessingException) {
        }
    }
}