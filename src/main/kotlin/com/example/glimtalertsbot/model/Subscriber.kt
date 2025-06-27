package com.example.glimtalertsbot.model

import jakarta.persistence.Entity
import jakarta.persistence.Id


@Entity
data class Subscriber(
    @Id
    val userId: String = "",
    //val userName: String //TODO: Add userName field if needed for future use
)
