package org.example.logic.entities

data class User(
    val username: String,
    val password: String,
    val hashedPassword: String,
    val role: Role = Role.MATE,
)