package com.example.glimtalertsbot.service

import com.example.glimtalertsbot.model.Subscriber
import com.example.glimtalertsbot.repository.SubscriberRepository
import org.springframework.stereotype.Service


@Service
class SubscriptionService(
    private val repository: SubscriberRepository
) {
    fun subscribe(userId: String) {
        repository.save(Subscriber(userId))
    }

    fun unsubscribe(userId: String) {
        repository.deleteById(userId)
    }

    fun isSubscribed(userId: String): Boolean =
        repository.existsById(userId)

    fun getAll(): List<String> =
        repository.findAll().map { it.userId }
}