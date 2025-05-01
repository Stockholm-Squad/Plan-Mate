package org.example.data.models

data class User(
    val username: String,
    val hashedPassword: String,
    val role: String
)