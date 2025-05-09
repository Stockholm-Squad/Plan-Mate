package org.example.data.repo

import data.models.TaskInProjectModel
import logic.models.entities.Task
import logic.models.exceptions.TaskExceptions
import org.example.data.datasources.mate_task_assignment_data_source.MateTaskAssignmentDataSource
import org.example.data.datasources.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.mapper.mapToTaskEntity
import org.example.data.mapper.mapToTaskModel
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.repository.TaskRepository
import java.util.*

class TaskRepositoryImp(
    private val taskDataSource: ITaskDataSource,
    private val mateTaskAssignment: MateTaskAssignmentDataSource,
    private val taskInProjectDataSource: ITaskInProjectDataSource,
) : TaskRepository {

    override suspend fun getAllTasks(): List<Task> =
        executeSafelyWithContext(
            onSuccess = {
                taskDataSource.read().mapNotNull { it.mapToTaskEntity() }
            },
            onFailure = { throw TaskExceptions.TasksNotFoundException() }
        )

    override suspend fun addTask(task: Task): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                taskDataSource.append(listOf(task.mapToTaskModel()))
            },
            onFailure = { throw TaskExceptions.TaskNotAddedException() }
        )

    override suspend fun editTask(task: Task): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                val currentTasks = taskDataSource.read().mapNotNull { it.mapToTaskEntity() }
                val updatedTasks = currentTasks.map { if (it.id == task.id) task else it }
                taskDataSource.overWrite(updatedTasks.map { it.mapToTaskModel() })
            },
            onFailure = { throw TaskExceptions.TaskNotEditException() }
        )

    override suspend fun deleteTask(id: UUID?): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                val currentTasks = taskDataSource.read().mapNotNull { it.mapToTaskEntity() }
                val updatedTasks = currentTasks.filterNot { it.id == id }
                taskDataSource.overWrite(updatedTasks.map { it.mapToTaskModel() })
            },
            onFailure = { throw TaskExceptions.TaskNotDeletedException() }
        )

    override suspend fun getTasksInProject(projectId: UUID): List<Task> =
        executeSafelyWithContext(
            onSuccess = {
                val taskLinks = taskInProjectDataSource.read()
                val taskIds = taskLinks.filter { it.projectId == projectId.toString() }.map { it.taskId }
                val allTasks = taskDataSource.read()
                allTasks.filter { taskIds.contains(it.id) }.mapNotNull { it.mapToTaskEntity() }
            },
            onFailure = { throw TaskExceptions.TasksNotFoundException() }
        )

    override suspend fun addTaskInProject(projectId: UUID, taskId: UUID): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                taskInProjectDataSource.append(
                    listOf(TaskInProjectModel(projectId.toString(), taskId.toString()))
                )
            },
            onFailure = { throw TaskExceptions.TaskNotAddedException() }
        )

    override suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                val current = taskInProjectDataSource.read()
                val updated = current.filterNot {
                    it.projectId == projectId.toString() && it.taskId == taskId.toString()
                }
                taskInProjectDataSource.overWrite(updated)
            },
            onFailure = { throw TaskExceptions.TaskNotDeletedException() }
        )

    override suspend fun getAllTasksByUserName(userName: String): List<Task> =
        executeSafelyWithContext(
            onSuccess = {
                val mateTaskAssignments =
                    mateTaskAssignment.getMateTaskAssignmentByUserName(userName)?.map { it.taskId }
                        ?: throw TaskExceptions.TasksNotFoundException()
                val tasks = taskDataSource.read()
                tasks.filter { mateTaskAssignments.contains(it.id) }.mapNotNull { it.mapToTaskEntity() }
            },
            onFailure = { throw TaskExceptions.TasksNotFoundException() }
        )
}
