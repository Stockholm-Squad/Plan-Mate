package org.example.data.models

data class UserModel(
    val id: String,
    val username: String,
    val hashedPassword: String,
    val role: String
)