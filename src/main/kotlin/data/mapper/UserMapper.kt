package org.example.data.mapper

import data.mapper.getAuditSystemType
import logic.model.entities.AuditSystem
import logic.model.entities.User
import logic.model.entities.UserRole
import org.example.data.models.UserModel
import org.example.data.utils.DateHandlerImp
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

