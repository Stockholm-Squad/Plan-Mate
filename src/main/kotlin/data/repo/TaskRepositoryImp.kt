package org.example.data.repo

import logic.model.entities.Task
import org.example.data.datasources.PlanMateDataSource
import org.example.data.entities.MateTaskAssignment
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository

class TaskRepositoryImp(
    private val taskDataSource: PlanMateDataSource<Task>,
    private val mateTaskAssignment: PlanMateDataSource<MateTaskAssignment>
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>> = readTasks()

    override fun createTask(task: Task): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks -> writeTasks(tasks + task) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )

    override fun editTask(task: Task): Result<Boolean> =
        readTasks().fold(
            onSuccess = { tasks ->
                taskDataSource.write(tasks.map { it.takeIf { it.id != task.id } ?: task })
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )


    override fun deleteTask(id: String?): Result<Boolean> =
        readTasks().fold(
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

    private fun readTasks(): Result<List<Task>> {
        return taskDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { throwable ->
                when (throwable) {
                    is PlanMateExceptions.DataException.FileNotExistException -> Result.success(emptyList())
                    else -> Result.failure(PlanMateExceptions.DataException.ReadException())
                }
            }
        )
    }

     fun writeTasks(tasks: List<Task>): Result<Boolean> {
        return taskDataSource.write(tasks).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
        )
    }
}





//    override fun getAllTasksInProjects(): Result<List<TaskInProject>> {
//        return taskInProjectDataSource.read()
//    }
//    override fun linkTaskToProject(taskInProject: TaskInProject): Result<Boolean> {
//        return taskInProjectDataSource.read().fold(
//            onSuccess = { links -> taskInProjectDataSource.write(links + taskInProject) },
//            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
//        )
//    }