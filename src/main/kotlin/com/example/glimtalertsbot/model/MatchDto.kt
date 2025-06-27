package com.example.glimtalertsbot.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class MatchDto(
    val id: Long,
    val dateTime: LocalDateTime,
    val homeTeam: String,
    val awayTeam: String,
    val location: String?,
    val league: String?,
    val isScheduled: Boolean,
    val channel: String
) {
    override fun toString(): String {
        val norwegianLocale = Locale("no", "NO")
        val weekday = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, norwegianLocale).replaceFirstChar { it.uppercaseChar() }
        val dateFormatted = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        val schedulingNote = if (isScheduled) "" else " ⚠️ Tidspunkt ikke bekreftet"
        val locationText = location?.let { "🏟️ $it\n" } ?: ""
        val leagueText = league?.let { "🏆 $it\n" } ?: ""
        val channelText = if (channel.isNotBlank()) "📺 $channel\n" else ""

        return """
            📅 $weekday $dateFormatted$schedulingNote
            $homeTeam - $awayTeam
            $locationText$leagueText$channelText
        """.trimIndent()
    }
}