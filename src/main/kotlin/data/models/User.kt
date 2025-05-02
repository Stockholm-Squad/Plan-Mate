package org.example.data.models

import java.util.*

data class User(
    val id: String,
    val username: String,
    val hashedPassword: String,
    val role: String
)