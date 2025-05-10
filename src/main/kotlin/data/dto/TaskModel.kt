package data.dto

data class TaskModel(
    val id: String ,
    val projectName: String,
    val name: String,
    val description: String,
    val stateId: String,
    val createdDate: String,
    val updatedDate: String
)
