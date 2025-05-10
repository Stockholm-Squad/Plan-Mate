package org.example.data.mapper

import data.dto.UserDto
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.utils.toSafeUUID

fun UserDto.mapToUserEntity(): User? {
    return User(
        id.toSafeUUID() ?: return null,
        username,
        hashedPassword,
        getRoleType(role)
    )
}

fun User.mapToUserModel(): UserDto {
    return UserDto(
        id.toString(),
        username,
        hashedPassword,
        userRole.toString()
    )
}

fun getRoleType(role: String): UserRole = when {
    role.equals("MATE", ignoreCase = true) -> UserRole.MATE
    role.equals("ADMIN", ignoreCase = true) -> UserRole.ADMIN
    else -> throw Exception("Unknown role type: $role")
}

