package org.example.data.repo

import logic.model.entities.Task
import data.models.MateTaskAssignment
import data.models.TaskInProject
import org.example.data.datasources.models.task_data_source.ITaskDataSource
import org.example.data.datasources.relations.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.relations.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.mapper.TaskMapper
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.ManageTasksUseCase

class TaskRepositoryImp(
    private val taskDataSource: ITaskDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
    private val taskInProjectDataSource: ITaskInProjectDataSource,
    private val manageTasksUseCase: ManageTasksUseCase,
    private val taskMapper: TaskMapper,
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>> = readTasks()

    override fun createTask(task: Task): Result<Boolean> {
        return taskDataSource.append(listOf(taskMapper.mapToTaskModel(task)))
    }

    override fun editTask(task: Task): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                writeTasks(tasks.map { it.takeIf { it.id != task.id } ?: task })
            },
            onFailure = { Result.failure(it) }
        )

    override fun deleteTask(id: String?): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                writeTasks(tasks.filterNot { it.id.toString() == id })
            },
            onFailure = { Result.failure(it) }
        )


    override fun getTasksInProject(projectId: String): Result<List<Task>> {
        return taskInProjectDataSource.read().fold(
            onSuccess = { taskInProject ->
                taskInProject.filter {
                    projectId == it.projectId
                }.map { it.taskId }.let { tasksIds ->
                    tasksIds.mapNotNull {
                        manageTasksUseCase.getTaskById(it).fold(
                            onSuccess = { task -> task },
                            onFailure = { null }
                        )
                    }.let {
                        Result.success(it)
                    }
                }
            }, onFailure = { Result.failure(it) })
    }

    override fun addTaskInProject(projectId: String, taskId: String): Result<Boolean> {
        return taskInProjectDataSource.append(listOf(TaskInProject(projectId = projectId, taskId = taskId)))
    }

    override fun deleteTaskFromProject(projectId: String, taskId: String): Result<Boolean> {
        return taskInProjectDataSource.read().fold(
            onSuccess = { tasksInProject ->
                tasksInProject.filterNot { taskInProject ->
                    (taskInProject.projectId == projectId) && (taskInProject.taskId == taskId)
                }.let { newTasksInProject ->
                    when (newTasksInProject.contains(TaskInProject(taskId, projectId))) {
                        true -> Result.success(false)
                        false -> taskInProjectDataSource.overWrite(newTasksInProject)
                    }
                }
            }, onFailure = { Result.failure(it) })
    }


    override fun getAllMateTaskAssignment(mateName: String): Result<List<MateTaskAssignment>> =
        mateTaskAssignment.read()

    private fun readTasks(): Result<List<Task>> {
        return taskDataSource.read().fold(
            onSuccess = { list -> Result.success(list.map { it1 -> taskMapper.mapToTaskEntity(it1) }) },
            onFailure = { throwable ->
                when (throwable) {
                    is PlanMateExceptions.DataException.FileNotExistException -> Result.success(emptyList())
                    else -> Result.failure(PlanMateExceptions.DataException.ReadException())
                }
            }
        )
    }

    fun writeTasks(tasks: List<Task>): Result<Boolean> {
        return tasks.map { task -> taskMapper.mapToTaskModel(task) }.let {
            taskDataSource.overWrite(it)
        }
    }
}