package org.example.ui.features.task

import logic.model.entities.Task
import logic.model.entities.User
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.TaskOptions
import org.example.ui.utils.UiMessages
import org.example.ui.utils.UiUtils
import java.util.*


class TaskManagerUi(
    private val reader: InputReader,
    private val printer: OutputPrinter,
    private val uiUtils: UiUtils,
    private val manageTasksUseCase: ManageTasksUseCase,
    private val manageStateUseCase: ManageStatesUseCase,
    private val manageTasksInProjectUseCase: ManageTasksInProjectUseCase,
) : UiLauncher {

    override fun launchUi(user: User?) {
        while (true) {
            printTaskOptionsMenu()
            if (enteredTaskOption(uiUtils.getEnteredOption(reader.readIntOrNull()))) break
        }
    }

    private fun enteredTaskOption(option: TaskOptions?): Boolean {
        when (option) {
            TaskOptions.SHOW_ALL_TASKS -> showAllTasks()
            TaskOptions.SHOW_TASK_BY_ID -> getTaskById()
            TaskOptions.CREATE_TASK -> createTask()
            TaskOptions.EDIT_TASK -> editTask()
            TaskOptions.DELETE_TASK -> deleteTask()
            TaskOptions.SHOW_TASKS_BY_PROJECT_ID -> showAllTasksInProject()
            TaskOptions.SHOW_MATE_TASK_ASSIGNMENTS -> showAllMateTaskAssignment()
            TaskOptions.EXIT -> {
                uiUtils.exit()
                return true
            }

            else -> uiUtils.invalidChoice()
        }
        return false
    }

    fun showAllTasks() {
        manageTasksUseCase.getAllTasks().fold(
            onSuccess = ::handleGetAllTasksSuccess,
            onFailure = ::handleFailure
        )
    }

    private fun handleGetAllTasksSuccess(tasks: List<Task>) {
        tasks.takeIf { it.isNotEmpty() }
            ?.let { printer.printTaskList(it) }
            ?: printer.showMessage(UiMessages.NO_TASK_FOUNDED)
    }

    fun getTaskById() {
        printer.showMessage(UiMessages.TASK_ID_PROMPT)

        val taskId = uiUtils.readNonBlankInputOrNull(reader)
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT)

        manageTasksUseCase.getTaskById(taskId).fold(
            onSuccess = { task -> printer.printTask(task) },
            onFailure = ::handleFailure
        )
    }

    fun createTask() {
        val (name, description, stateName) = readTaskInput()
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

        val stateId = manageStateUseCase.getProjectStateIdByName(stateName)
            ?: return printer.showMessage(UiMessages.INVALID_TASK_STATE_INPUT)

        val task = Task(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            stateId = stateId,
            createdDate = DateHandlerImp().getCurrentDateTime(),
            updatedDate = DateHandlerImp().getCurrentDateTime()
        )

        manageTasksUseCase.createTask(task).fold(
            onSuccess = { printer.printTask(task) },
            onFailure = ::handleFailure
        )
    }

    fun editTask() {
        printer.showMessage(UiMessages.TASK_ID_PROMPT)
        val taskId = uiUtils.readNonBlankInputOrNull(reader)
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT)

        val existingTask = manageTasksUseCase.getTaskById(taskId).getOrNull()
            ?: return printer.showMessage(UiMessages.NO_TASK_FOUNDED)

        val (newName, newDescription, newStateName) = readTaskInput()
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

        val newStateId = manageStateUseCase.getProjectStateIdByName(newStateName)
            ?: return printer.showMessage(UiMessages.INVALID_STATE_NAME)

        val updatedTask = existingTask.copy(
            name = newName,
            description = newDescription,
            stateId = newStateId,
            updatedDate = DateHandlerImp().getCurrentDateTime()
        )

        manageTasksUseCase.editTask(updatedTask).fold(
            onSuccess = { printer.printTask(updatedTask) },
            onFailure = ::handleFailure
        )
    }

    fun deleteTask() {
        printer.showMessage(UiMessages.TASK_ID_PROMPT)

        val taskId = uiUtils.readNonBlankInputOrNull(reader)
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT)

        if (manageTasksUseCase.getTaskById(taskId).getOrNull() == null) {
            return printer.showMessage(UiMessages.NO_TASK_FOUNDED)
        }

        manageTasksUseCase.deleteTask(taskId).fold(
            onSuccess = { printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY) },
            onFailure = ::handleFailure
        )
    }

    fun showAllTasksInProject() {
        printer.showMessage(UiMessages.PROJECT_ID_PROMPT)

        val projectId = uiUtils.readNonBlankInputOrNull(reader)
            ?: return printer.showMessage(UiMessages.EMPTY_PROJECT_ID_INPUT)

        manageTasksInProjectUseCase.getTasksAssignedToProject(projectId).fold(
            onSuccess = { tasks: List<Task> ->
                tasks.takeUnless { it.isEmpty() }
                    ?.let { printer.printTaskList(it) }
                    ?: printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT)
            },
            onFailure = ::handleFailure
        )
    }

    fun showAllMateTaskAssignment() {
        printer.showMessage(UiMessages.USER_NAME_PROMPT)

        val userName = uiUtils.readNonBlankInputOrNull(reader)
            ?: printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT)

        getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName as String).fold(
            onSuccess = { assignments -> printer.printMateTaskAssignments(assignments) },
            onFailure = ::handleFailure
        )
    }

    private fun readTaskInput(): Triple<String, String, String>? {
        printer.showMessage(UiMessages.TASK_NAME_PROMPT)
        val name = reader.readStringOrNull()?.takeIf { it.isNotBlank() } ?: run {
            printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT)
            return null
        }

        printer.showMessage(UiMessages.TASK_DESCRIPTION_PROMPT)
        val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() } ?: run {
            printer.showMessage(UiMessages.EMPTY_TASK_DESCRIPTION_INPUT)
            return null
        }

        printer.showMessage(UiMessages.TASK_STATE_PROMPT)
        val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() } ?: run {
            printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT)
            return null
        }

        return Triple(name, description, stateName)
    }

    private fun handleFailure(throwable: Throwable) {
        printer.showMessage("Error: ${throwable.message}")
    }

    private fun printTaskOptionsMenu() {
        printer.showMessage("========================= Tasks Option =========================")
        printer.showMessage("Please Choose an option. Pick a number between 0 and 7!\n")

        TaskOptions.entries
            .forEach { println("${it.option}. ${it.label}") }

        printer.showMessage("-----------------------------------------------------")
        printer.showMessage("Choose an option: ")
    }
}