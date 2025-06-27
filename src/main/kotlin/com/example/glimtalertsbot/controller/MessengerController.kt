package com.example.glimtalertsbot.controller

import com.example.glimtalertsbot.model.MatchDto
import com.example.glimtalertsbot.service.MatchService
import com.example.glimtalertsbot.service.MessengerService
import com.example.glimtalertsbot.service.SubscriptionService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException

@RestController
@RequestMapping("/webhook")
class MessengerController(
    private val matchService: MatchService,
    private val messengerService: MessengerService,
    private val subscriptionService: SubscriptionService,
    @Value("\${messenger.verify-token}") val verifyToken: String
) {

    @GetMapping
    fun verify(
        @RequestParam("hub.mode") mode: String?,
        @RequestParam("hub.verify_token") token: String?,
        @RequestParam("hub.challenge") challenge: String?
    ): ResponseEntity<String> {
        return if (mode == "subscribe" && token == verifyToken) {
            ResponseEntity.ok(challenge)
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }


    @PostMapping
    fun handleWebhook(@RequestBody body: String) {
        val json = jacksonObjectMapper().readTree(body)
        val messaging = json["entry"]?.get(0)?.get("messaging")?.get(0)

        val senderId = messaging?.get("sender")?.get("id")?.asText()
        val text = messaging?.get("message")?.get("text")?.asText()?.trim()?.lowercase()

        println("📩 $senderId says: $text")

        if (senderId != null && text != null) {
            when (text.lowercase()) {
                "neste" -> {
                    val match = getNextMatchWithRetries(senderId)
                    val reply = match?.toString() ?: "⚽ Finner ingen kamper i nærmeste fremtid 🤷‍♀️"
                    messengerService.sendMessage(senderId, reply)
                }

/*
                //alle does not work because messenger has a max length of 2000 for messages sent.
                // todo: implement toShortString() in MatchDto, paginate results, or send multiple messages
                "alle" -> {
                    val matches = matchService.getAllUpcomingMatches()
                    val reply = matches.joinToString("\n\n") { it.toString() }
                        .ifBlank { "⚽ Å nei, jeg finner ingen kamper som skal spilles." }
                    messengerService.sendMessage(senderId, reply)
                }
*/
                "subscribe", "abonner" -> {
                    subscriptionService.subscribe(senderId)
                    messengerService.sendMessage(senderId, "✅ Du abonnerer nå på kampvarsler! " +
                            "Jeg sender deg en melding klokken 10:00 på kampdagen. (Svar 'stopp' eller 'unsubscribe' for å avslutte abonnementet.)")
                }

                "unsubscribe", "stopp" -> {
                    subscriptionService.unsubscribe(senderId)
                    messengerService.sendMessage(senderId, "🚫 Jeg kommer ikke til å sende deg flere kampvarsler. " +
                            "Du kan abonnere igjen når som helst ved å skrive 'subscribe' eller 'abonner'.")
                }

                else -> {
                    messengerService.sendMessage(
                        senderId,
                        "❓ Bip bop, jeg skjønner ikke hva du mener. Skriv 'neste' for informasjon om neste Glimt-kamp, " +
                                " 'abonner' for å abonnere på kampvarsler, " + "eller 'stopp' for å avslutte abonnementet."
                    )
                }
            }
        }
    }

    fun getNextMatchWithRetries(senderId: String, maxRetries: Int = 3): MatchDto? {
        val delaySeconds = listOf(5L, 10L, 20L)

        for (attempt in 0 until maxRetries) {
            try {
                return matchService.getNextMatch()
            } catch (e: Exception) {
                if (e is HttpServerErrorException && e.statusCode.value() == 502) {
                    messengerService.sendMessage(senderId, "⏳ Roboten som henter kamper sover, vent litt...")
                    Thread.sleep(delaySeconds.getOrElse(attempt) { 20 } * 1000)
                } else {
                    println("❌ Klarte ikke hente kampen: ${e.message}")
                    break
                }
            }
        }

        messengerService.sendMessage(senderId, "⚠️ Klarte ikke hente kampdata. Prøv igjen om litt.")
        return null
    }
}