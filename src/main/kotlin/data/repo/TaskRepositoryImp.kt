package org.example.data.repo

import logic.model.entities.Task
import org.example.data.datasources.PlanMateDataSource
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository

class TaskRepositoryImp(
    private val taskDataSource: PlanMateDataSource<Task>,
    private val taskInProjectDataSource: PlanMateDataSource<TaskInProject>,
    private val mateTaskAssignment: PlanMateDataSource<MateTaskAssignment>
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>> =
        taskDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun createTask(task: Task): Result<Boolean> =
        taskDataSource.read().fold(
            onSuccess = { tasks -> taskDataSource.write(tasks + task) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun editTask(task: Task): Result<Boolean> =
        taskDataSource.read().fold(
            onSuccess = { tasks ->
                val updatedTasks = tasks.map { if (it.id == task.id) task else it }
                taskDataSource.write(updatedTasks)
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun deleteTask(id: String): Result<Boolean> =
        taskDataSource.read().fold(
            onSuccess = { tasks ->
                val updatedTasks = tasks.filterNot { it.id == id }
                taskDataSource.write(updatedTasks)
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun getAllMateTaskAssignment(mateName: String): Result<List<MateTaskAssignment>> =
        mateTaskAssignment.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun getAllTasksByProjectId(projectId: String): Result<List<TaskInProject>> =
        taskInProjectDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )
}