package logic.models.entities
import java.util.*

data class Project(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val stateId: UUID
)