package org.example.data.mapper

import logic.models.entities.User
import logic.models.entities.UserRole
import org.example.data.models.UserModel
import org.example.logic.usecase.extention.toSafeUUID

fun UserModel.mapToUserEntity(): User? {
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

fun User.mapToUserModel(): UserModel = UserModel(
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

