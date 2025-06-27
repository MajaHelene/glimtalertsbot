package com.example.glimtalertsbot.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MessengerService(
    private val restTemplate: RestTemplate,
    @Value("\${messenger.page-token}") private val pageAccessToken: String
) {
    private val sendMessageUrl = "https://graph.facebook.com/v18.0/me/messages?access_token=$pageAccessToken"

    fun sendMessage(recipientId: String, text: String) {
        val payload = mapOf(
            "recipient" to mapOf("id" to recipientId),
            "message" to mapOf("text" to text)
        )

        try {
            restTemplate.postForObject(sendMessageUrl, payload, String::class.java)
            println("✅ Sent message to $recipientId: $text")
        } catch (e: Exception) {
            println("❌ Failed to send message to $recipientId: ${e.message}")
        }
    }
}