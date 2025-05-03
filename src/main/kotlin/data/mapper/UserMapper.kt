package org.example.data.mapper

import logic.model.entities.User
import logic.model.entities.UserRole
import org.example.data.models.UserModel
import org.example.logic.usecase.extention.toSafeUUID

fun UserModel.mapToUserEntity(): User =
    User(
        id.toSafeUUID(),
        username,
        hashedPassword,
        getRoleType(role)
    )

fun User.mapToUserModel(): UserModel = UserModel(
    id.toString(),
    username,
    hashedPassword,
    userRole.toString()
)

fun getRoleType(role: String): UserRole = when {
    role.equals("Role.MATE", ignoreCase = true) -> UserRole.MATE
    role.equals("Role.ADMIN", ignoreCase = true) -> UserRole.ADMIN
    else -> throw Exception("Unknown role type: $role")
}

