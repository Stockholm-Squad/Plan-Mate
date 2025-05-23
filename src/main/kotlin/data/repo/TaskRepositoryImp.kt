package org.example.data.repo

import org.example.data.mapper.mapToTaskEntity
import org.example.data.mapper.mapToTaskModel
import org.example.data.source.TaskDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.TaskNotAddedException
import org.example.logic.TaskNotDeletedException
import org.example.logic.TaskNotUpdatedException
import org.example.logic.TasksNotFoundException
import org.example.logic.entities.Task
import org.example.logic.repository.TaskRepository
import java.util.*

class TaskRepositoryImp(
    private val taskDataSource: TaskDataSource,
) : TaskRepository {

    override suspend fun getAllTasks(): List<Task> = tryToExecute(
        function = { taskDataSource.getAllTasks().mapNotNull { it.mapToTaskEntity() } },
        onSuccess = { listOfTasks -> listOfTasks },
        onFailure = { throw TasksNotFoundException() }
    )

    override suspend fun addTask(task: Task): Boolean = tryToExecute(
        function = { taskDataSource.addTask(task.mapToTaskModel()) },
        onSuccess = { isAdded -> isAdded },
        onFailure = { throw TaskNotAddedException() }
    )

    override suspend fun updateTask(task: Task): Boolean = tryToExecute(
        function = { taskDataSource.updateTask(task.mapToTaskModel()) },
        onSuccess = { isUpdated -> isUpdated },
        onFailure = { throw TaskNotUpdatedException() }
    )

    override suspend fun deleteTask(taskId: UUID?): Boolean = tryToExecute(
        function = { taskDataSource.deleteTask(taskId.toString()) },
        onSuccess = { isDeleted -> isDeleted },
        onFailure = { throw TaskNotDeletedException() }
    )

    override suspend fun getTasksInProject(projectId: UUID): List<Task> = tryToExecute(
        function = {
            taskDataSource.getTasksInProject(projectId.toString()).mapNotNull { task -> task.mapToTaskEntity() }
        },
        onSuccess = { listOfTasks -> listOfTasks },
        onFailure = { throw TasksNotFoundException() }
    )


    override suspend fun addTaskInProject(projectId: UUID, taskId: UUID): Boolean = tryToExecute(
        function = {
            taskDataSource.addTaskInProject(
                projectId = projectId.toString(),
                taskId = taskId.toString()
            )
        },
        onSuccess = { isAddedToProject -> isAddedToProject },
        onFailure = { throw TaskNotAddedException() }
    )

    override suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean = tryToExecute(
        function = {
            taskDataSource.deleteTaskFromProject(
                projectId = projectId.toString(),
                taskId = taskId.toString()
            )
        },
        onSuccess = { isDeletedFromProject -> isDeletedFromProject },
        onFailure = { throw TaskNotDeletedException() }
    )

    override suspend fun getAllTasksByUserName(username: String): List<Task> = tryToExecute(
        function = { taskDataSource.getAllTasksByUserName(username).mapNotNull { it.mapToTaskEntity() } },
        onSuccess = { listOfTasks -> listOfTasks },
        onFailure = { throw TasksNotFoundException() }
    )
}
