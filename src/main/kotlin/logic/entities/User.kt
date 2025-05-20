package org.example.logic.entities

import java.util.*


data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: String,
    val userRole: UserRole = UserRole.MATE,
)

enum class UserRole {
    ADMIN,
    MATE,
    UNKNOWN;

    companion object {
        fun getUserRole(roleName: String): UserRole {
            return entries.find { it.name == roleName } ?: UNKNOWN
        }
    }
}