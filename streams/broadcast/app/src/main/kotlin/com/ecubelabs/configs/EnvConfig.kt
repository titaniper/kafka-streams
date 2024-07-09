package com.ecubelabs.configs

class EnvConfig {
    val kafkaBrokers: Array<String> = arrayOf<String>(System.getenv("KAFKA_BROKERS") ?: "")
    val apiHost: String = System.getenv("API_HOST") ?: ""
    val accessToken: String
    init {
        val nodeEnv = System.getenv("NODE_ENV") ?: "development"
        // HACK: 토큰은 어떻게 관리하지?
        accessToken = if (nodeEnv == "production") {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NDMxIiwiaWF0IjoxNzE0MDE0ODI1fQ.MZrVG-q8_3bdnxfryVPiKb18uiC8jNGU2M5tttWV5qg"
        } else {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NDMxIiwiaWF0IjoxNzA1NTQwOTYxfQ.5t-EV_sW6O7g57ExlfBIbIYHWeDs2gETIKErYcQ9V2A"
        }
    }
}
