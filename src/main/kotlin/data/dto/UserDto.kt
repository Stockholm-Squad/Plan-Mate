package data.dto

data class UserDto(
    val id: String,
    val username: String,
    val hashedPassword: String,
    val role: String
)