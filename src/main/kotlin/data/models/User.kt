package org.example.data.models

import java.util.*

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: String,
    val role: String
)