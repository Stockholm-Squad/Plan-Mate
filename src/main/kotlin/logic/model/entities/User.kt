package logic.model.entities

data class User(
    val username: String,
    val hashedPassword: String,
    val role: Role = Role.MATE
)