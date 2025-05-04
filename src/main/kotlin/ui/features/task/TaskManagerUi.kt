package org.example.ui.features.task

import logic.model.entities.Task
import logic.model.entities.User
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.UiMessages
import org.example.ui.utils.UiUtils
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.ui.utils.TaskOptions
import java.util.*


class TaskManagerUi(
    private val reader: InputReader,
    private val printer: OutputPrinter,
    private val uiUtils: UiUtils,
    private val manageTasksUseCase: ManageTasksUseCase,
    private val manageStateUseCase: ManageStatesUseCase,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageTasksInProjectUseCase: ManageTasksInProjectUseCase,
) : UiLauncher {

    override fun launchUi(user: User?) {
        while (true) {
            printTaskOptionsMenu()
            val optionChoice = reader.readIntOrNull()
            if (enteredTaskOption(getEnteredOption(optionChoice))) break
        }
    }

    private fun enteredTaskOption(option: TaskOptions?): Boolean {
        when (option) {
            TaskOptions.SHOW_ALL_TASKS -> showAllTasks()
            TaskOptions.SHOW_TASK_BY_ID -> getTaskByName()
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

    fun getTaskByName() {
        val taskName = getTaskName()
        manageTasksUseCase.getTaskByName(taskName).fold(
            onSuccess = { task -> printer.printTask(task) },
            onFailure = ::handleFailure
        )
    }

    fun createTask() {
        val projectName = getProjectByName()
        val (name, description, stateName) = readTaskInput()
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

        val stateId = manageStateUseCase.getProjectStateIdByName(stateName)
            ?: return printer.showMessage(UiMessages.INVALID_TASK_STATE_INPUT)

        val task = Task(
            projectName = projectName,
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
        val taskName = getTaskName()

        val existingTask = manageTasksUseCase.getTaskByName(taskName).getOrNull()
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
        val taskName = getTaskName()

        if (manageTasksUseCase.getTaskByName(taskName).getOrNull() == null) {
            return printer.showMessage(UiMessages.NO_TASK_FOUNDED)
        }

        manageTasksUseCase.deleteTaskByName(taskName).fold(
            onSuccess = { printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY) },
            onFailure = ::handleFailure
        )
    }

    fun showAllTasksInProject() {

        val projectName = getProjectByName()

        manageTasksInProjectUseCase.getTasksInProjectByName(projectName).fold(
            onSuccess = { tasks: List<Task> ->
                tasks.takeUnless { it.isEmpty() }
                    ?.let { printer.printTaskList(it) }
                    ?: printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT)
            },
            onFailure = ::handleFailure
        )
    }

    fun showAllMateTaskAssignment() {
        val userName = getUserName()
        manageTasksInProjectUseCase.getAllTasksByUserName(userName).fold(
            onSuccess = { assignments -> printer.printMateTaskAssignments(assignments) },
            onFailure = ::handleFailure
        )
    }

    private fun readTaskInput(): Triple<String, String, String>? {
        while (true) {
            printer.showMessage(UiMessages.TASK_NAME_PROMPT)
            val name = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            if (name == null) {
                printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT)
                continue
            }

            printer.showMessage(UiMessages.TASK_DESCRIPTION_PROMPT)
            val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            if (description == null) {
                printer.showMessage(UiMessages.EMPTY_TASK_DESCRIPTION_INPUT)
                continue
            }

            printer.showMessage(UiMessages.TASK_STATE_PROMPT)
            val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            if (stateName == null) {
                printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT)
                continue
            }

            return Triple(name, description, stateName)
        }
    }

    private fun getProjectByName(): String {
        while (true) {
            printer.showMessage(UiMessages.PROJECT_NAME_PROMPT)
            val projectNameInput = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            if (projectNameInput == null) {
                printer.showMessage(UiMessages.EMPTY_PROJECT_NAME_INPUT)
                continue
            }

            if (manageProjectUseCase.getProjectByName(projectNameInput).getOrNull() == null) {
                printer.showMessage(UiMessages.NO_PROJECT_FOUNDED)
                continue
            }

            return projectNameInput
        }
    }

    private fun getTaskName(): String {
        while (true) {
            printer.showMessage(UiMessages.TASK_NAME_PROMPT)
            val taskName = uiUtils.readNonBlankInputOrNull(reader)
            if (taskName == null) {
                printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT)
                continue
            }
            return taskName
        }
    }

    private fun getUserName(): String {
        while (true) {
            printer.showMessage(UiMessages.USER_NAME_PROMPT)
            val userName = uiUtils.readNonBlankInputOrNull(reader)
            if (userName == null) {
                printer.showMessage(UiMessages.EMPTY_USER_NAME_INPUT)
                continue
            }
            return userName
        }
    }

    private fun handleFailure(throwable: Throwable) {
        printer.showMessage("Error: ${throwable.message}")
    }

    private fun getEnteredOption(option: Int?) = TaskOptions.entries.find { it.option == option }


    private fun printTaskOptionsMenu() {
        printer.showMessage("========================= Tasks Option =========================")
        printer.showMessage("Please Choose an option. Pick a number between 0 and 7!\n")

        TaskOptions.entries
            .forEach { println("${it.option}. ${it.label}") }

        printer.showMessage("-----------------------------------------------------")
        printer.showMessage("Choose an option: ")
    }
}