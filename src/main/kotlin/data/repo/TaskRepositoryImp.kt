package org.example.data.repo

import logic.model.entities.Task
import org.example.data.datasources.PlanMateDataSource
import data.models.MateTaskAssignment
import org.example.data.datasources.models.task_data_source.ITaskDataSource
import org.example.data.datasources.relations.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.mapper.TaskMapper
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository

class TaskRepositoryImp(
    private val taskDataSource: ITaskDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
    private val taskMapper: TaskMapper,
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>> = readTasks()

    override fun createTask(task: Task): Result<Boolean> {
        return taskDataSource.append(listOf(taskMapper.mapToTaskModel(task))).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
        )
    }

    override fun editTask(task: Task): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                writeTasks(tasks.map { it.takeIf { it.id != task.id } ?: task })
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun deleteTask(id: String?): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                writeTasks(tasks.filterNot { it.id.toString() == id })
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )


    override fun getAllMateTaskAssignment(mateName: String): Result<List<MateTaskAssignment>> =
        mateTaskAssignment.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

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
            taskDataSource.overWrite(it).fold(
                onSuccess = { Result.success(it) },
                onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
            )
        }
    }
}