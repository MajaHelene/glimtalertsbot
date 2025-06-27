package com.example.glimtalertsbot.service

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.example.glimtalertsbot.model.MatchDto
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MatchService(
    private val restTemplate: RestTemplate
) {
    private val apiBaseUrl = "http://localhost:8081"

    private val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun getAllUpcomingMatches(): List<MatchDto> {
        val response = restTemplate.getForObject(
            "$apiBaseUrl/matches/upcoming",
            String::class.java
        ) ?: return emptyList()

        return objectMapper.readValue(response)
    }

    fun getNextMatch(): MatchDto? {
        val response = restTemplate.getForObject(
            "$apiBaseUrl/matches/next",
            String::class.java
        ) ?: return null
        return objectMapper.readValue(response)
    }
}