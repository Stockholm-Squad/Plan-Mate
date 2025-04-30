package logic.usecase.authentication

import logic.model.entities.Role
import modle.buildUser

private val allUsers = listOf(
    buildUser(username = "johnDoe", hashedPassword = "hashedPass1", role = Role.MATE),
    buildUser(username = "janeSmith", hashedPassword = "hashedPass2", role = Role.MATE),
    buildUser(username = "techGuru", hashedPassword = "hashedPass3", role = Role.MATE),
    buildUser(username = "adminUser", hashedPassword = "hashedPass4", role = Role.ADMIN)
)

fun getAllUsers() = allUsers