package logic.model.entities

import kotlinx.serialization.Serializable
import org.example.data.mapper.UUIDSerializer
import java.util.*

// Define this once in your project

@Serializable
data class User(
    @Serializable(UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: String,
    val role: Role = Role.MATE
)