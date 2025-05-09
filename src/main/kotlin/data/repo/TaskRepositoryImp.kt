package org.example.data.repo

import logic.models.entities.Task
import logic.models.exceptions.TaskExceptions
import org.example.data.datasources.mate_task_assignment_data_source.MateTaskAssignmentDataSource
import org.example.data.datasources.task_In_project_data_source.TaskInProjectDataSource
import org.example.data.datasources.task_data_source.TaskDataSource
import org.example.data.mapper.mapToTaskEntity
import org.example.data.mapper.mapToTaskModel
import org.example.data.utils.tryToExecute
import org.example.logic.repository.TaskRepository
import java.util.*

class TaskRepositoryImp(
    private val taskDataSource: TaskDataSource,
    private val mateTaskAssignment: MateTaskAssignmentDataSource,
    private val taskInProjectDataSource: TaskInProjectDataSource,
) : TaskRepository {

    override suspend fun getAllTasks(): List<Task> =
        tryToExecute(
            function = { taskDataSource.getAllTasks().mapNotNull { it.mapToTaskEntity() } },
            onSuccess = { it },
            onFailure = { throw TaskExceptions.TasksNotFoundException() }
        )

    override suspend fun addTask(task: Task): Boolean =
        tryToExecute(
            function = { taskDataSource.addTask(task.mapToTaskModel()) },
            onSuccess = { true },
            onFailure = { throw TaskExceptions.TaskNotAddedException() }
        )

    override suspend fun editTask(task: Task): Boolean =
        tryToExecute(
            function = { taskDataSource.editTask(task.mapToTaskModel()) },
            onSuccess = { true },
            onFailure = { throw TaskExceptions.TaskNotEditException() }
        )

    override suspend fun deleteTask(id: UUID?): Boolean =
        tryToExecute(
            function = { taskDataSource.deleteTask(id.toString()) },
            onSuccess = { true },
            onFailure = { throw TaskExceptions.TaskNotDeletedException() }
        )

    override suspend fun getTasksInProject(projectId: UUID): List<Task> =
        tryToExecute(
            function = {
                val project = taskInProjectDataSource.getTasksInProjectByProjectId(projectId.toString())
                val taskIds = project.map { it.taskId }
                taskDataSource.getTasksByIds(taskIds).mapNotNull { it.mapToTaskEntity() }
            },
            onSuccess = { it },
            onFailure = { throw TaskExceptions.TasksNotFoundException() }
        )


    override suspend fun addTaskInProject(projectId: UUID, taskId: UUID): Boolean =
        tryToExecute(
            function = {
                taskInProjectDataSource.addTaskInProject(
                    projectId.toString(),
                    taskId.toString()
                )
            },
            onSuccess = { true },
            onFailure = { throw TaskExceptions.TaskNotAddedException() }
        )

    override suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean =
        tryToExecute(
            function = {
                taskInProjectDataSource.deleteTaskFromProject(
                    projectId.toString(),
                    taskId.toString()
                )
            },
            onSuccess = { true },
            onFailure = { throw TaskExceptions.TaskNotDeletedException() }
        )

    override suspend fun getAllTasksByUserName(userName: String): List<Task> =
        tryToExecute(
            function = {
                val mateTaskAssignments = mateTaskAssignment.getUsersMateTaskByUserName(userName).map { it.taskId }
                taskDataSource.getTasksByIds(mateTaskAssignments).mapNotNull { it.mapToTaskEntity() }
            },
            onSuccess = { it },
            onFailure = { throw TaskExceptions.TasksNotFoundException() }
        )
}
