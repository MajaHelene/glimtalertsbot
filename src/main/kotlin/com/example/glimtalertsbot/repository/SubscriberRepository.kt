package com.example.glimtalertsbot.repository

import com.example.glimtalertsbot.model.Subscriber
import org.springframework.data.repository.CrudRepository

interface SubscriberRepository : CrudRepository<Subscriber, String>