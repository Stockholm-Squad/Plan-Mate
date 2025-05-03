package modle

import logic.model.entities.UserRole
import logic.model.entities.User

fun buildUser(
    username: String?,
    hashedPassword: String?,
    userRole: UserRole?
): User {
    return User(username = username.orEmpty(), hashedPassword = hashedPassword.orEmpty(), userRole = userRole ?: UserRole.MATE)
}