package com.ecubelabs.libs.k8s

import java.io.FileWriter
import java.io.IOException
import java.time.Instant


object HealthCheckApp {
    private const val HEALTH_CHECK_FILE_PATH = "/tmp/healthy"

    @Throws(IOException::class)
    fun execute(logic: Runnable) {
        try {
            generateHealthyFile()
            logic.run()
        } catch (e: Exception) {
        }
    }

    @Throws(IOException::class)
    private fun generateHealthyFile() {
        FileWriter(HEALTH_CHECK_FILE_PATH).use { writer ->
            writer.write(Instant.now().epochSecond.toString())
        }
    }
}