package org.example.logic.entities

import java.util.*

data class State(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
)
