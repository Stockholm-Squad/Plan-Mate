package logic.model.entities

import java.util.*

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: String,
    val userRole: UserRole = UserRole.MATE
)