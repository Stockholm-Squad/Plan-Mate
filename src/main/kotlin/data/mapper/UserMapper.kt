package org.example.data.mapper

import data.dto.UserDto
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.utils.toSafeUUID

fun UserDto.mapToUserEntity(): User? {
    return User(
        id = id.toSafeUUID() ?: return null,
        username = username,
        hashedPassword = hashedPassword,
        userRole = UserRole.getUserRole(role)
    )
}

fun User.mapToUserModel(): UserDto {
    return UserDto(
        id = id.toString(),
        username = username,
        hashedPassword = hashedPassword,
        role = userRole.toString()
    )
}