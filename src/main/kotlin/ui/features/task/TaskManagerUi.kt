package org.example.ui.features.task

import logic.model.entities.Task
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.utils.DateHandler
import org.example.utils.TaskOptions
import java.util.UUID

class TaskManagerUi(
    private val manageTasksUseCase: ManageTasksUseCase,
    private val reader: InputReader,
    private val printer: OutputPrinter
) {

    fun startTaskManager() {
        while (true) {
            printMenu()
            if (handleEnteredTaskOption(reader.readIntOrNull())) break
        }
    }

    private fun handleEnteredTaskOption(option: Int?): Boolean {
        when (getEnteredOption(option)) {
            TaskOptions.SHOW_ALL_TASKS -> showAllTasks()

            TaskOptions.GET_TASK_BY_ID -> getTaskById()

            TaskOptions.CREATE_TASK -> createTask()

            TaskOptions.EDIT_TASK -> editTask()

            TaskOptions.DELETE_TASK -> deleteTask()

            TaskOptions.SHOW_TASKS_BY_PROJECT -> showAllTasksInProject()

            TaskOptions.SHOW_TASKS_BY_USER -> showAllMateTaskAssignment()

            TaskOptions.EXIT -> {
                exit()
                true
            }

            else -> invalidChoice()
        }
        return false
    }

    fun showAllTasks() {
        manageTasksUseCase.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.takeUnless { it.isEmpty() }
                    ?.also { printer.showMessage("All Tasks:") }
                    ?.forEachIndexed { index, task ->
                        printer.showMessage("${index + 1}. ID: ${task.id}, Name: ${task.name}, Description: ${task.description}, State: ${task.stateId}")
                    }
                    ?: printer.showMessage("No tasks found.")
            },
            onFailure = { exception ->
                printer.showMessage("Error: ${exception.message}")
            }
        )
    }

    fun getTaskById() {
        printer.showMessage("Enter task ID:")
        val taskId = reader.readStringOrNull()
            ?: return printer.showMessage("Task ID cannot be empty.")

        manageTasksUseCase.getTaskById(taskId).fold(
            onSuccess = { task ->
                printer.showMessage("Task Details:")
                printer.showMessage("ID: ${task.id}, Name: ${task.name}, Description: ${task.description}, State: ${task.stateId}")
            },
            onFailure = { exception ->
                printer.showMessage("Error: ${exception.message}")
            }
        )
    }

    private fun createTask(): Result<Boolean> {
        printer.showMessage("Let's create a task!")

        printer.showMessage("Enter task name:")
        val taskName = readStringOrNull()
            ?: return Result.failure(Throwable("Task name cannot be empty."))

        printer.showMessage("Enter task description:")
        val taskDescription = readStringOrNull()
            ?: return Result.failure(Throwable("Task description cannot be empty."))

        showStateOfProject()

        printer.showMessage("Enter state ID:")
        val stateId = readStringOrNull()
            ?: return Result.failure(Throwable("State ID cannot be empty."))

        val taskId = UUID.randomUUID().toString()
        val now = DateHandler().getCurrentDateTime()

        // Construct the Task object
        val newTask = Task(
            id = taskId,
            name = taskName,
            description = taskDescription,
            stateId = stateId,
            createdDate = now,
            updatedDate = now
        )

        // Pass the Task object to the use case
        return manageTasksUseCase.createTask(newTask)
            .onSuccess {
                printer.showMessage("Task created successfully!")
            }
            .onFailure { exception ->
                printer.showMessage("Error creating task: ${exception.message}")
            }
    }

    private fun editTask() {
        printer.showMessage("Enter task ID to edit:")
        val taskId = readStringOrNull()
            ?: return printer.showMessage("Task ID cannot be empty.")

        val existingTaskResult = manageTasksUseCase.getTaskById(taskId)
        val existingTask = existingTaskResult.getOrNull()
            ?: return printer.showMessage("Error: Task with ID $taskId not found.")

        printer.showMessage("Enter new task name (leave blank to keep current):")
        val newName = readStringOrNull() ?: existingTask.name

        printer.showMessage("Enter new task description (leave blank to keep current):")
        val newDescription = readStringOrNull() ?: existingTask.description

        printer.showMessage("Enter new state ID (leave blank to keep current):")
        val newStateId = readStringOrNull() ?: existingTask.stateId

        val updatedDate = DateHandler().getCurrentDateTime()

        val updatedTask = existingTask.copy(
            name = newName,
            description = newDescription,
            stateId = newStateId,
            updatedDate = updatedDate
        )

        manageTasksUseCase.editTask(updatedTask)
            .onSuccess {
                printer.showMessage("Task updated successfully!")
            }
            .onFailure { exception ->
                printer.showMessage("Error updating task: ${exception.message}")
            }
    }

    private fun deleteTask() {
        printer.showMessage("Enter task ID to delete:")
        val taskId = readStringOrNull()
            ?: return printer.showMessage("Task ID cannot be empty.")

        manageTasksUseCase.deleteTask(taskId)
            .onSuccess {
                printer.showMessage("Task deleted successfully!")
            }
            .onFailure { exception ->
                printer.showMessage("Error deleting task: ${exception.message}")
            }
    }

    private fun showAllMateTaskAssignment() {
        printer.showMessage("Enter user ID (mate ID):")
        val userId = readStringOrNull()
            ?: return printer.showMessage("User ID cannot be empty.")

        manageTasksUseCase.getAllMateTaskAssignment(userId)
            .onSuccess { tasks ->
                tasks.takeUnless { it.isEmpty() }
                    ?.also { printer.showMessage("Tasks assigned to user ID $userId:") }
                    ?.forEachIndexed { index, task ->
                        printer.showMessage("${index + 1}. Task ID: ${task.taskId}")
                    }
                    ?: printer.showMessage("No tasks found for user ID: $userId")
            }
            .onFailure { exception ->
                printer.showMessage("Error: ${exception.message}")
            }
    }

    private fun showAllTasksInProject() {
        printer.showMessage("Enter project ID:")
        val projectId = readStringOrNull()
            ?: return printer.showMessage("Project ID cannot be empty.")

        manageTasksUseCase.getAllTasksByProjectId(projectId)
            .onSuccess { tasks ->
                tasks.takeUnless { it.isEmpty() }
                    ?.also { printer.showMessage("Tasks in project '$projectId':") }
                    ?.forEachIndexed { index, task ->
                        printer.showMessage("${index + 1}. Task ID: ${task.taskId}, Name: ${task.projectId}")
                    }
                    ?: printer.showMessage("No tasks found for project '$projectId'.")
            }
            .onFailure { exception ->
                printer.showMessage("Error: ${exception.message}")
            }
    }

    private fun showStateOfProject() {
        printer.showMessage("Enter project ID:")
        val projectId = readStringOrNull()
            ?: return printer.showMessage("Project ID cannot be empty.")

        manageTasksUseCase.getAllStatesByProjectId(projectId)
            .onSuccess { states ->
                states.takeUnless { it.isEmpty() }
                    ?.also { printer.showMessage("States for project '$projectId':") }
                    ?.forEachIndexed { index, (taskId, stateId) ->
                        printer.showMessage("${index + 1}. Task ID: $taskId, State ID: $stateId")
                    }
                    ?: printer.showMessage("No states found for project '$projectId'.")
            }
            .onFailure { exception ->
                printer.showMessage("Error: ${exception.message}")
            }
    }

    private fun readStringOrNull(): String? =
        reader.readStringOrNull()?.takeUnless { it.isBlank() }

    private fun getEnteredOption(option: Int?) = TaskOptions.entries.find { it.option == option }

    private fun invalidChoice() {
        println("😕 Oops! That’s not on the options. Pick a number between 0 and 7!")
    }

    private fun exit() {
        println("👋 Goodbye")
    }

    private fun printMenu() {
        println(
            """
            ========================= Tasks Option =========================
            Please Choose an option. Pick a number between 0 and 7!
            
            1. Show all Tasks 
            2. Get task by id 
            3. Create task
            4. Edit task
            5. Delete Task
            6. Show all tasks at specific project
            7. Show all tasks assignment to user
            0. Exit (Don't gooo! 😢)
            -----------------------------------------------------
            Choose an option: 
        """.trimIndent()
        )
    }
}
