package org.example.data.mapper

import logic.model.entities.Role
import org.example.data.models.User
import org.example.data.extention.toSafeUUID

class UserMapper {
    fun mapToUserEntity(user: User): logic.model.entities.User = logic.model.entities.User(
       user.id.toSafeUUID(),
        user.username,
        user.hashedPassword,
        getRoleType(user.role)
    )

    fun mapToUserModel(user: logic.model.entities.User): User = User(
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
