package data.dto

data class TaskDto(
    val id: String,
    val projectTitle: String,
    val title: String,
    val description: String,
    val stateId: String,
    val createdDate: String,
    val updatedDate: String
)
