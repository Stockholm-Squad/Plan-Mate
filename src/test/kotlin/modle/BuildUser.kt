package modle

import logic.model.entities.Role
import logic.model.entities.User

fun buildUser(
    username: String?,
    hashedPassword: String?,
    role: Role?
): User {
    return User(username = username.orEmpty(), hashedPassword = hashedPassword.orEmpty(), role = role ?: Role.MATE)
}