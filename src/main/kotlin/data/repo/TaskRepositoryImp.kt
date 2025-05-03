package org.example.data.repo

import data.models.TaskInProject
import logic.model.entities.Task
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.extention.toSafeUUID
import org.example.data.mapper.TaskMapper
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.ManageTasksUseCase
import java.util.*

class TaskRepositoryImp(
    private val taskDataSource: ITaskDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
    private val taskInProjectDataSource: ITaskInProjectDataSource,
    private val taskMapper: TaskMapper,
    private val manageTasksUseCase: ManageTasksUseCase,
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
                    projectId == it.projectId.toSafeUUID()
                }.map { it.taskId }.let { tasksIds ->
                    getTasks(tasksIds).let { tasks ->
                        Result.success(tasks as List<Task>)
                    }
                }
            }, onFailure = { Result.failure(it) })
    }

    private fun getTasks(tasksIds: List<String>) = tasksIds.mapNotNull {
        this.getAllTasks().getOrNull()?.filter { true }
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


    override fun getAllTasksByUserName(userName: String): Result<List<Task>> =
        mateTaskAssignment.read().fold(
            onSuccess = { taskToMate ->
                taskToMate.filter {
                    userName == it.userName
                }.map { it.taskId }.let { tasksIds ->
                    tasksIds.mapNotNull {
                        manageTasksUseCase.getTaskById(it.toSafeUUID()).getOrNull()
                    }.let {
                        Result.success(it)
                    }
                }
            }, onFailure = { Result.failure(it) }
        )

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