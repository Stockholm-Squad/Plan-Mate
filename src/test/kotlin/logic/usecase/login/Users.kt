package logic.usecase.login

import logic.model.entities.Role
import modle.buildUser

private val allUsers = listOf(
    buildUser(username = "johnDoe", hashedPassword = "6c6b8a98fc1503009200747f9ca0420e", role = Role.MATE),
    buildUser(username = "janeSmith", hashedPassword = "3e4be391bc444a6e7cdeacdb7348f328", role = Role.MATE),
    buildUser(username = "techGuru", hashedPassword = "d0f7ae41fc64eac3a92c8cd7d40c8674", role = Role.MATE),
    buildUser(username = "adminUser", hashedPassword = "c9f6b9ff2ecb981d21610d9e4c01faae", role = Role.ADMIN)
)

fun getAllUsers() = allUsers