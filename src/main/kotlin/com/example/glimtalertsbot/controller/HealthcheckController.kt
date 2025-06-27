package com.example.glimtalertsbot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthcheckController {
    @GetMapping("/")
    fun health(): String = "The bot is OK"
}