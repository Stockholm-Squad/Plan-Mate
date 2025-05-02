package org.example.data.repo

import data.models.MateTaskAssignment
import logic.model.entities.Task
import data.models.TaskInProject
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.task_In_project_data_source.ITaskInProjectDataSource
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
                        manageTasksUseCase.getTaskById(it).getOrNull()
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


    override fun getAllMateTaskAssignment(mateName: String): Result<List<Task>> =
        mateTaskAssignment.read().fold(
            onSuccess = { taskToMate ->
                taskToMate.filter {
                    mateName == it.userName
                }.map { it.taskId }.let { tasksIds ->
                    tasksIds.mapNotNull {
                        manageTasksUseCase.getTaskById(it).getOrNull()
                    }.let {
                        Result.success(it)
                    }
                }
            }, onFailure = { Result.failure(it) }
        )

    override fun getAllTaskByMateId(mateId: String): Result<List<Task>> {
        return mateTaskAssignment.read().fold(
            onSuccess = { assignments ->
                assignments.filter { it.userName == mateId }
                    .map { it.taskId }
                    .let { taskIds ->
                        taskIds.mapNotNull { taskId ->
                            manageTasksUseCase.getTaskById(taskId).getOrNull()
                        }.let { tasks ->
                            Result.success(tasks)
                        }
                    }
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun getAllMateByTaskId(taskId: String): Result<List<String>> {
        return mateTaskAssignment.read().fold(
            onSuccess = { assignments ->
                Result.success(
                    assignments.filter { it.taskId == taskId }
                        .map { it.userName }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun addMateTaskAssignment(mateName: String, taskId: String): Result<Boolean> {
        return mateTaskAssignment.append(
            listOf(MateTaskAssignment(userName = mateName, taskId = taskId))
        )
    }

    override fun deleteMateTaskAssignment(mateName: String, taskId: String): Result<Boolean> {
        return mateTaskAssignment.read().fold(
            onSuccess = { assignments ->
                val newAssignments = assignments.filterNot {
                    it.userName == mateName && it.taskId == taskId
                }
                when (newAssignments.size == assignments.size) {
                    true -> Result.success(false)
                    false -> mateTaskAssignment.overWrite(newAssignments)
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    private fun readTasks(): Result<List<Task>> {
        return taskDataSource.read().fold(
            onSuccess = { list -> Result.success(list.map { it1 -> taskMapper.mapToTaskEntity(it1) }) },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    fun writeTasks(tasks: List<Task>): Result<Boolean> {
        return tasks.map { task -> taskMapper.mapToTaskModel(task) }.let {
            taskDataSource.overWrite(it)
        }
    }
}