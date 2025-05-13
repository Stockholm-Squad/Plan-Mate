package logic.usecase.audit

import org.example.logic.entities.EntityType
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import java.util.*

// ==== UUIDs ====
val userId: UUID = UUID.fromString("5df64cdc-824b-4b93-ac3a-5fda9528466c")
val userId1: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
val userId2: UUID = UUID.fromString("a5a85f64-6737-4562-b9fc-3c963f66abc1")
val entityId: UUID = UUID.fromString("e2c592e6-2618-405f-96b4-03bba272416d")
val projectId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")

// ==== Entities ====
val entityType = EntityType.TASK

// ==== User ====
val user = User(
    id = userId,
    username = "plan",
    hashedPassword = "bd9fa9edbeff8f0b88a6f26ce7665953",
    userRole = UserRole.MATE
)

// ==== Strings ====
const val entityName = "Task 001"
const val additionalInfo = "Urgent task"
const val existingName = "Old Name"
const val newName = "New Name"
const val projectName = "project"
const val taskName = "task"
const val newDescription = "Updated description"
const val newStateName = "Completed"
const val addedDescription = "Added Task"
const val updateDescription = "Updated Task"
const val deletedDescription = "Deleted Task"
