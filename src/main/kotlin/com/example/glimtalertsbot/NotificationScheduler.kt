package com.example.glimtalertsbot

import com.example.glimtalertsbot.service.MatchService
import com.example.glimtalertsbot.service.MessengerService
import com.example.glimtalertsbot.service.SubscriptionService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class NotificationScheduler(
    private val matchService: MatchService,
    private val subscriptionService: SubscriptionService,
    private val messengerService: MessengerService
) {

    @Scheduled(cron = "0 15 10 * * *", zone = "Europe/Oslo") // Every day at 10:00 Oslo time
    fun notifySubscribersIfMatchToday() {
        val today = LocalDate.now()
        val match = matchService.getNextMatch()

        if (match != null && match.dateTime.toLocalDate() == today) {
            val message = "📣 Det er Glimt-kamp i dag!\n\n${match.toString()}"
            val subscribers = subscriptionService.getAll()

            if (subscribers.isEmpty()) {
                println("👥 Ingen abonnenter å varsle i dag.")
                return
            }

            println("📤 Sender kampvarsling til ${subscribers.size} abonnenter...")

            subscribers.forEach { userId ->
                messengerService.sendMessage(userId, message)
            }
        } else {
            println("📅 Ingen kamp i dag.")
        }
    }
}