package org.example.data.repo

import data.models.MateTaskAssignment
import data.models.TaskInProject
import logic.model.entities.Task
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.mapper.TaskMapper
import org.example.logic.repository.TaskRepository
import java.util.*

class TaskRepositoryImp(
    private val taskDataSource: ITaskDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
    private val taskInProjectDataSource: ITaskInProjectDataSource,
    private val taskMapper: TaskMapper,
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>> = readTasks()

    override fun addTask(task: Task): Result<Boolean> {
        return taskDataSource.append(listOf(taskMapper.mapToTaskModel(task)))
    }

    override fun editTask(task: Task): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                writeTasks(tasks.map { it.takeIf { it.id != task.id } ?: task })
            },
            onFailure = { Result.failure(it) }
        )

    override fun deleteTask(id: UUID?): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                writeTasks(tasks.filterNot { it.id.toString() == id.toString() })
            },
            onFailure = { Result.failure(it) }
        )


    override fun getTasksInProject(projectId: UUID): Result<List<Task>> {
        return taskInProjectDataSource.read().fold(
            onSuccess = { taskInProject ->
                taskInProject.filter {
                    projectId.toString() == it.projectId
                }.map { it.taskId }.let { tasksIds ->
                    getTasksFromTasksIds(tasksIds)
                }
            }, onFailure = { Result.failure(it) })
    }

    override fun addTaskInProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskInProjectDataSource.append(
            listOf(
                TaskInProject(
                    projectId = projectId.toString(),
                    taskId = taskId.toString()
                )
            )
        )
    }

    override fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskInProjectDataSource.read().fold(
            onSuccess = { tasksInProject ->
                tasksInProject.filterNot { taskInProject ->
                    (taskInProject.projectId == projectId.toString()) && (taskInProject.taskId == taskId.toString())
                }.let { newTasksInProject ->
                    when (newTasksInProject.contains(TaskInProject(taskId.toString(), projectId.toString()))) {
                        true -> Result.success(false)
                        false -> taskInProjectDataSource.overWrite(newTasksInProject)
                    }
                }
            }, onFailure = { Result.failure(it) })
    }


    override fun getAllTasksByUserName(userName: String): Result<List<Task>> {
        return mateTaskAssignment.read().fold(
            onSuccess = { userToTaskList ->
                getTasksFromTasksIds(getTasksIdsAssignedToUser(userToTaskList, userName))
            },
            onFailure = { Result.failure(it) }
        )
    }

    private fun getTasksFromTasksIds(tasksIds: List<String>): Result<List<Task>> {
        return taskDataSource.read().fold(
            onSuccess = { tasks ->
                tasks.filter { tasksIds.contains(it.id) }
                    .map { taskMapper.mapToTaskEntity(it) }
                    .let { Result.success(it) }
            },
            onFailure = { Result.failure(it) }
        )
    }

    private fun getTasksIdsAssignedToUser(
        userToTaskList: List<MateTaskAssignment>,
        userName: String
    ): List<String> {
        return userToTaskList.filter { userToTask ->
            userToTask.userName == userName
        }.map { it.taskId }
    }

    private fun readTasks(): Result<List<Task>> {
        return taskDataSource.read().fold(
            onSuccess = { list -> Result.success(list.map { it1 -> taskMapper.mapToTaskEntity(it1) }) },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    private fun writeTasks(tasks: List<Task>): Result<Boolean> {
        return tasks.map { task -> taskMapper.mapToTaskModel(task) }.let {
            taskDataSource.overWrite(it)
        }
    }
}