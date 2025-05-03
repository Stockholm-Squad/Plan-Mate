package org.example.data.mapper

import logic.model.entities.UserRole
import org.example.data.models.UserModel
import org.example.logic.usecase.extention.toSafeUUID

class UserMapper {
    fun mapToUserEntity(userModel: UserModel): logic.model.entities.User = logic.model.entities.User(
        userModel.id.toSafeUUID(),
        userModel.username,
        userModel.hashedPassword,
        getRoleType(userModel.role)
    )

    fun mapToUserModel(user: logic.model.entities.User): UserModel = UserModel(
        user.id.toString(),
        user.username,
        user.hashedPassword,
        user.userRole.toString()
    )

    fun getRoleType(role: String): UserRole = when {
        role.equals("Role.MATE", ignoreCase = true) -> UserRole.MATE
        role.equals("Role.ADMIN", ignoreCase = true) -> UserRole.ADMIN
        else -> throw Exception("Unknown role type: $role")
    }
}
