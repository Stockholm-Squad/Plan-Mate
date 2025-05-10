package org.example.data.mapper

import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import data.dto.UserDto
import org.example.logic.usecase.extention.toSafeUUID

fun UserDto.mapToUserEntity(): User? {
    return try {
        User(
            id.toSafeUUID(),
            username,
            hashedPassword,
            getRoleType(role)
        )
    } catch (throwable: Throwable) {
        null
    }
}

fun User.mapToUserModel(): UserDto = UserDto(
    id.toString(),
    username,
    hashedPassword,
    userRole.toString()
)

fun getRoleType(role: String): UserRole = when {
    role.equals("MATE", ignoreCase = true) -> UserRole.MATE
    role.equals("ADMIN", ignoreCase = true) -> UserRole.ADMIN
    else -> throw Exception("Unknown role type: $role")
}

