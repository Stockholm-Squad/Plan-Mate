package org.example.data.mapper

import logic.model.entities.Role
import org.example.data.models.UserModel
import org.example.data.extention.toSafeUUID

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
        user.role.toString()
    )

    fun getRoleType(role: String): Role = when {
        role.equals("Role.MATE", ignoreCase = true) -> Role.MATE
        role.equals("Role.ADMIN", ignoreCase = true) -> Role.ADMIN
        else -> throw Exception("Unknown role type: $role")
    }
}
