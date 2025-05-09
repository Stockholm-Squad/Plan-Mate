package modle

import org.example.logic.entities.User
import org.example.logic.entities.UserRole

fun buildUser(
    username: String?,
    hashedPassword: String?,
    userRole: UserRole?
): User {
    return User(username = username.orEmpty(), hashedPassword = hashedPassword.orEmpty(), userRole = userRole ?: UserRole.MATE)
}