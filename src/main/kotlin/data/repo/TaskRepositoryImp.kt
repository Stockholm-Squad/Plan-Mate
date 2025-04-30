package org.example.data.repo

import logic.model.entities.Task
import org.example.data.datasources.PlanMateDataSource
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.logic.repository.TaskRepository

class TaskRepositoryImp(
    private val taskDataSource: PlanMateDataSource<Task>,
    private val taskInProjectDataSource: PlanMateDataSource<TaskInProject>,
    private val mateTaskAssignment: PlanMateDataSource<MateTaskAssignment>
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>> =
        taskDataSource.read("tasks.csv").fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )

    override fun createTask(task: Task): Result<Boolean> =
        taskDataSource.read("tasks.csv").fold(
            onSuccess = { tasks -> taskDataSource.write(tasks + task) },
            onFailure = { exception -> Result.failure(exception) }
        )

    override fun editTask(task: Task): Result<Boolean> =
        taskDataSource.read("tasks.csv").fold(
            onSuccess = { tasks ->
                val updatedTasks = tasks.map { if (it.id == task.id) task else it }
                taskDataSource.write(updatedTasks)
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    override fun deleteTask(id: String?): Result<Boolean> =
        taskDataSource.read("tasks.csv").fold(
            onSuccess = { tasks ->
                val updatedTasks = tasks.filterNot { it.id == id }
                taskDataSource.write(updatedTasks)
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    override fun getAllMateTaskAssignment(mateName: String): Result<List<MateTaskAssignment>> =
        mateTaskAssignment.read("users_assigned_to_tasks.csv").fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )

    override fun getAllTasksByProjectId(projectId: String): Result<List<TaskInProject>> =
        taskInProjectDataSource.read("tasks_in_projects.csv").fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )
}