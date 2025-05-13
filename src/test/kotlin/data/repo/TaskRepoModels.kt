package data.repo

import data.dto.TaskDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.logic.entities.Task
import java.util.UUID

val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

val taskId1 = UUID.randomUUID()
val taskId2 = UUID.randomUUID()
val taskId3 = UUID.randomUUID()

val stateId1 = UUID.randomUUID()
val stateId2 = UUID.randomUUID()
val stateId3 = UUID.randomUUID()
val task = Task(
    id = taskId1,
    projectName = "Project A",
    title = "Task 1",
    description = "First task",
    stateId = stateId1,
    createdDate = currentTime,
    updatedDate = currentTime
)
val taskDto =  TaskDto(
    id = taskId1.toString(),
    projectName = "Project A",
    title = "Task 1",
    description = "First task",
    stateId = stateId1.toString(),
    createdDate = currentTime.toString(),
    updatedDate = currentTime.toString()
)
val tasksList = listOf(
    Task(
        id = taskId1,
        projectName = "Project A",
        title = "Task 1",
        description = "First task",
        stateId = stateId1,
        createdDate = currentTime,
        updatedDate = currentTime
    ), Task(
        id = taskId2,
        projectName = "Project B",
        title = "Task 2",
        description = "Second task",
        stateId = stateId2,
        createdDate = currentTime,
        updatedDate = currentTime
    ), Task(
        id = taskId3,
        projectName = "Project C",
        title = "Task 3",
        description = "Third task",
        stateId = stateId3,
        createdDate = currentTime,
        updatedDate = currentTime
    )
)
val taskDtoList = listOf(
    TaskDto(
        id = taskId1.toString(),
        projectName = "Project A",
        title = "Task 1",
        description = "First task",
        stateId = stateId1.toString(),
        createdDate = currentTime.toString(),
        updatedDate = currentTime.toString()
    ), TaskDto(
        id = taskId2.toString(),
        projectName = "Project B",
        title = "Task 2",
        description = "Second task",
        stateId = stateId2.toString(),
        createdDate = currentTime.toString(),
        updatedDate = currentTime.toString()
    ), TaskDto(
        id = taskId3.toString(),
        projectName = "Project C",
        title = "Task 3",
        description = "Third task",
        stateId = stateId3.toString(),
        createdDate = currentTime.toString(),
        updatedDate = currentTime.toString()
    )
)