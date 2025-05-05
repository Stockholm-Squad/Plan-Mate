package modle

import logic.models.entities.User
import logic.models.entities.UserRole

fun buildUser(
    username: String?,
    hashedPassword: String?,
    userRole: UserRole?
): User {
    return User(username = username.orEmpty(), hashedPassword = hashedPassword.orEmpty(), userRole = userRole ?: UserRole.MATE)
}