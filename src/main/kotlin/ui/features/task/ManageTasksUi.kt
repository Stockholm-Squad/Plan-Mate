package org.example.ui.features.task

import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.utils.TaskOptions

class ManageTasksUi(private val manageTasksUseCase: ManageTasksUseCase,
                    private val printer: OutputPrinter,
                    private val reader: InputReader) {

    fun startManageTasks() {
        showTaskOptions()
        while (true) {
            getOption(reader.readIntOrNull()).let {
                if (it) return
            }
        }
    }

    private fun showTaskOptions() {
        printer.showMessage("1- show all tasks\n" +
                "2- show task by id\n" +
                "3- create task\n" +
                "4- edit task\n" +
                "5- delete task\n" +
                "6- get all tasks by project id\n" +
                "7- make task assignment \n")
    }

    fun handleEnteredTaskOption(option: Int?): Boolean {
        return when (val selectedOption = getEnteredOption(option)) {
            TaskOptions.SHOW_ALL_TASKS -> showAllTasks()
            TaskOptions.SHOW_TASK_BY_ID -> showTaskById()
            TaskOptions.CREATE_TASK -> createTask()
            TaskOptions.EDIT_TASK -> editTask()
            TaskOptions.DELETE_TASK -> deleteTask()
            TaskOptions.SHOW_TASKS_BY_PROJECT_ID -> getAllTasksByProjectId()
            TaskOptions.GET_MATE_TASK_ASSIGNMENTS -> getMateTaskAssignments()
            TaskOptions.EXIT -> {
                exist()
                return true
            }

            else -> invalidChoice()
        }
        return false
    }



    private fun invalidChoice() = printer.showMessage("Oops! That's not on the menu. Pick a number between 0 to 7!")

    private fun exist() = printer.showMessage("Returning to main menu...")

    private fun getEnteredOption(option: Int?): Result<TaskOptions> =
        TaskOptions.entries
            .find { it.option == option }
            .toResult { Throwable("Invalid option") }

    private fun mateTaskAssignment(): Result<Boolean> = Result.failure(Throwable("Not yet implemented"))

    private fun editTask(): Result<Boolean> = Result.failure(Throwable("Not yet implemented"))

    fun showAllTasks(): Result<List<Task>> =
        manageTasksUseCase.getAllTasks()
            .onSuccess { printer.showMessage("Tasks loaded successfully.") }
            .onFailure { printer.showMessage("Failed to load tasks.") }

    fun createTask(): Result<Boolean> {
        printer.showMessage("Please Enter Name Of Task : ")
        val taskName = reader.readStringOrNull()
        printer.showMessage("Please Enter Description Of Task : ")
        val description = reader.readStringOrNull()
        printer.showMessage("Please Enter StateId Of Task : ")
        val stateId = reader.readStringOrNull()

        return Task(name = taskName, description = description, stateId = stateId)
            .toResult { Throwable("Invalid input for creating task") }
            .flatMap { manageTasksUseCase.createTask(it) }
    }

    fun showTaskById(): Result<Task> {
        printer.showMessage("Please Enter Task ID : ")
        return reader.readStringOrNull()
            .toResult { Throwable("Invalid Task ID") }
            .flatMap { manageTasksUseCase.getTaskById(it) }
    }

    private fun deleteTask(): Result<Boolean> {
        printer.showMessage("Please Enter Task ID to Delete : ")
        return reader.readStringOrNull()
            .toResult { Throwable("Invalid Task ID for deletion") }
            .flatMap { manageTasksUseCase.deleteTask(it) }
    }

    private fun getAllTasksByProjectId(): Result<List<TaskInProject>> {
        printer.showMessage("Please Enter Project ID : ")
        return reader.readStringOrNull()
            .toResult { Throwable("Invalid Project ID") }
            .flatMap { manageTasksUseCase.getAllTasksByProjectId(it) }
    }

    fun getMateTaskAssignments(): Result<List<MateTaskAssignment>> {
        printer.showMessage("Please Enter User Name : ")
        return reader.readStringOrNull()
            .toResult { Throwable("Invalid User Name") }
            .flatMap { manageTasksUseCase.getAllTasksByUserId(it) }
    }
}

// Extension function to convert nullable values to Result with custom error handling
fun <T> T?.toResult(error: () -> Throwable): Result<T> =
    this?.let { Result.success(it) } ?: Result.failure(error())

// Extension function to map over Result and handle failure case
fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    this.fold(
        onSuccess = { transform(it) },
        onFailure = { Result.failure(it) }
    )
