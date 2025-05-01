package org.example.ui.features.task

import logic.model.entities.Task
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.utils.UiMessages
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
            TaskOptions.SHOW_TASK_BY_ID -> getTaskById()
            TaskOptions.CREATE_TASK -> createTask()
            TaskOptions.EDIT_TASK -> editTask()
            TaskOptions.DELETE_TASK -> deleteTask()
           // TaskOptions.SHOW_TASKS_BY_PROJECT_ID -> showAllTasksInProject()
          //  TaskOptions.SHOW_MATE_TASK_ASSIGNMENTS -> showAllMateTaskAssignment()
            TaskOptions.EXIT -> {
                exit()
                return true
            }
            else -> invalidChoice()
        }
        return false
    }

    fun showAllTasks() {
        manageTasksUseCase.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.takeUnless { it.isEmpty() }
                    ?.also { printer.showMessage(UiMessages.ALL_TASKS.message) }
                    ?.forEachIndexed { index, task ->
                        printer.showMessage("${index + 1}. ID: ${task.id}, Name: ${task.name}, Description: ${task.description}, State: ${task.stateId}")
                    }
                    ?: printer.showMessage(UiMessages.NO_TASKS_FOUND.message)
            },
            onFailure = { exception ->
                printer.showMessage(UiMessages.GENERIC_ERROR.message.format(exception.message))
            }
        )
    }

    fun getTaskById() {
        printer.showMessage(UiMessages.TASK_ID_PROMPT.message)
        val taskId = reader.readStringOrNull()
            ?: return printer.showMessage(UiMessages.TASK_ID_EMPTY.message)

        manageTasksUseCase.getTaskById(taskId).fold(
            onSuccess = { task ->
                printer.showMessage(UiMessages.TASK_DETAILS.message)
                printer.showMessage("ID: ${task.id}, Name: ${task.name}, Description: ${task.description}, State: ${task.stateId}")
            },
            onFailure = { exception ->
                printer.showMessage(UiMessages.GENERIC_ERROR.message.format(exception.message))
            }
        )
    }

    fun createTask(): Result<Boolean> {
        printer.showMessage(UiMessages.CREATE_TASK_INTRO.message)

        printer.showMessage(UiMessages.ENTER_TASK_NAME.message)
        val taskName = readStringOrNull()
            ?: return Result.failure(Throwable(UiMessages.TASK_NAME_EMPTY.message))

        printer.showMessage(UiMessages.ENTER_TASK_DESCRIPTION.message)
        val taskDescription = readStringOrNull()
            ?: return Result.failure(Throwable(UiMessages.TASK_DESCRIPTION_EMPTY.message))

      //  showStateOfProject()

        printer.showMessage(UiMessages.ENTER_STATE_ID.message)
        val stateId = readStringOrNull()
            ?: return Result.failure(Throwable(UiMessages.STATE_ID_EMPTY.message))

        val taskId = UUID.randomUUID().toString()
        val now = DateHandler().getCurrentDateTime()

        val newTask = Task(
            id = taskId,
            name = taskName,
            description = taskDescription,
            stateId = stateId,
            createdDate = now,
            updatedDate = now
        )

        return manageTasksUseCase.createTask(newTask)
            .onSuccess {
                printer.showMessage(UiMessages.TASK_CREATED_SUCCESS.message)
            }
            .onFailure { exception ->
                printer.showMessage(UiMessages.TASK_CREATION_ERROR.message.format(exception.message))
            }
    }

   fun editTask() {
        printer.showMessage(UiMessages.TASK_ID_PROMPT.message)
        val taskId = readStringOrNull()
            ?: return printer.showMessage(UiMessages.TASK_ID_EMPTY.message)

        val existingTask = manageTasksUseCase.getTaskById(taskId).getOrNull()
            ?: return printer.showMessage(UiMessages.TASK_NOT_FOUND.message.format(taskId))

        printer.showMessage(UiMessages.EDIT_TASK_NAME_PROMPT.message)
        val newName = readStringOrNull() ?: existingTask.name

        printer.showMessage(UiMessages.EDIT_TASK_DESCRIPTION_PROMPT.message)
        val newDescription = readStringOrNull() ?: existingTask.description

        printer.showMessage(UiMessages.EDIT_TASK_STATE_ID_PROMPT.message)
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
                printer.showMessage(UiMessages.TASK_UPDATED_SUCCESS.message)
            }
            .onFailure { exception ->
                printer.showMessage(UiMessages.TASK_UPDATE_ERROR.message.format(exception.message))
            }
    }

     fun deleteTask() {
        printer.showMessage(UiMessages.TASK_ID_PROMPT.message)
        val taskId = readStringOrNull()
            ?: return printer.showMessage(UiMessages.TASK_ID_EMPTY.message)

        manageTasksUseCase.deleteTask(taskId)
            .onSuccess {
                printer.showMessage(UiMessages.TASK_DELETED_SUCCESS.message)
            }
            .onFailure { exception ->
                printer.showMessage(UiMessages.TASK_DELETION_ERROR.message.format(exception.message))
            }
    }

//     fun showAllMateTaskAssignment() {
//        printer.showMessage(UiMessages.TASK_ASSIGNMENT_PROMPT.message)
//        val userId = readStringOrNull()
//            ?: return printer.showMessage(UiMessages.TASK_ASSIGNMENT_EMPTY.message)
//
//        manageTasksUseCase.getAllMateTaskAssignment(userId)
//            .onSuccess { tasks ->
//                tasks.takeUnless { it.isEmpty() }
//                    ?.also { printer.showMessage(UiMessages.TASKS_FOR_USER.message.format(userId)) }
//                    ?.forEachIndexed { index, task ->
//                        printer.showMessage("${index + 1}. Task ID: ${task.taskId}")
//                    }
//                    ?: printer.showMessage(UiMessages.NO_TASKS_FOR_USER.message.format(userId))
//            }
//            .onFailure { exception ->
//                printer.showMessage(UiMessages.GENERIC_ERROR.message.format(exception.message))
//            }
//    }

//    fun showAllTasksInProject() {
//        printer.showMessage(UiMessages.ENTER_PROJECT_ID.message)
//        val projectId = readStringOrNull()
//            ?: return printer.showMessage(UiMessages.PROJECT_ID_EMPTY.message)
//
//        manageTasksUseCase.getAllTasksByProjectId(projectId)
//            .onSuccess { tasks ->
//                tasks.takeUnless { it.isEmpty() }
//                    ?.also { printer.showMessage(UiMessages.PROJECT_TASKS.message.format(projectId)) }
//                    ?.forEachIndexed { index, task ->
//                        printer.showMessage("${index + 1}. Task ID: ${task.taskId}, Name: ${task.projectId}")
//                    }
//                    ?: printer.showMessage(UiMessages.NO_PROJECT_TASKS.message.format(projectId))
//            }
//            .onFailure { exception ->
//                printer.showMessage(UiMessages.GENERIC_ERROR.message.format(exception.message))
//            }
//    }

//     fun showStateOfProject() {
//        printer.showMessage(UiMessages.ENTER_PROJECT_ID.message)
//        val projectId = readStringOrNull()
//            ?: return printer.showMessage(UiMessages.PROJECT_ID_EMPTY.message)
//
//        manageTasksUseCase.getAllStatesByProjectId(projectId)
//            .onSuccess { states ->
//                states.takeUnless { it.isEmpty() }
//                    ?.also { printer.showMessage(UiMessages.STATES_FOR_PROJECT.message.format(projectId)) }
//                    ?.forEachIndexed { index, (taskId, stateId) ->
//                        printer.showMessage("${index + 1}. Task ID: $taskId, State ID: $stateId")
//                    }
//                    ?: printer.showMessage(UiMessages.NO_STATES_FOR_PROJECT.message.format(projectId))
//            }
//            .onFailure { exception ->
//                printer.showMessage(UiMessages.GENERIC_ERROR.message.format(exception.message))
//            }
//
//         }

    private fun readStringOrNull(): String? =
        reader.readStringOrNull()?.takeUnless { it.isBlank() }

    private fun getEnteredOption(option: Int?) = TaskOptions.entries.find { it.option == option }

    private fun invalidChoice() {
        printer.showMessage(UiMessages.INVALID_OPTION.message)
    }

    private fun exit() {
        printer.showMessage(UiMessages.GOODBYE.message)
    }

    private fun printMenu() {
        printer.showMessage(UiMessages.MENU_HEADER.message)
    }}