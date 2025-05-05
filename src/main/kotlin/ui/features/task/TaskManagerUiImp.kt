package org.example.ui.features.task

import logic.model.entities.Task
import logic.model.entities.User
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.common.utils.TaskOptions
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.common.utils.UiUtils
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class TaskManagerUiImp(
    private val reader: InputReader,
    private val printer: OutputPrinter,
    private val uiUtils: UiUtils,
    private val manageTasksUseCase: ManageTasksUseCase,
    private val manageStateUseCase: ManageStatesUseCase,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageTasksInProjectUseCase: ManageTasksInProjectUseCase,
) : TaskManagerUi {
    private var currentUser: User? = null

    override fun launchUi(user: User?) {
        this.currentUser = user

        if (currentUser == null) {
            printer.showMessage(UiMessages.INVALID_USER)
            return
        }

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
            TaskOptions.SHOW_TASKS_BY_PROJECT_NAME -> showAllTasksInProject()
            TaskOptions.SHOW_MATE_TASK_ASSIGNMENTS -> showAllMateTaskAssignment()
            TaskOptions.EXIT -> {
                uiUtils.exit()
                return true
            }

            else -> uiUtils.invalidChoice()
        }
        return false
    }

    override fun showAllTasks() {
        manageTasksUseCase.getAllTasks().fold(
            onSuccess = ::handleGetAllTasksSuccess,
            onFailure = ::handleFailure
        )
    }

    private fun handleGetAllTasksSuccess(tasks: List<Task>) {
        tasks.takeIf { it.isNotEmpty() }
            ?.let { printer.printTaskList(it) }
            ?: printer.showMessage(UiMessages.NO_TASK_FOUND)
    }

    private fun getTaskByName() {
        val taskName = getTaskName()
        manageTasksUseCase.getTaskByName(taskName).fold(
            onSuccess = { task -> printer.printTask(task) },
            onFailure = ::handleFailure
        )
    }

    override fun createTask() {

        val projectName = getProjectByName()
        if (projectName.isEmpty()) return
        val (name, description, stateName) = readCreateTaskInput()
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

        val userId = currentUser?.id
            ?: return printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

        manageProjectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                manageTasksUseCase.createTask(task, userId).fold(
                    onSuccess = {
                        printer.printTask(task)
                        manageTasksInProjectUseCase.addTaskToProject(project.id, task.id)
                    },
                    onFailure = ::handleFailure
                )
            },
            onFailure = {
                printer.showMessage(UiMessages.NO_PROJECT_FOUNDED)
                return
            }
        )
    }

    override fun editTask() {
        val taskName = getTaskName()

        val existingTask = manageTasksUseCase.getTaskByName(taskName).getOrNull()
            ?: return printer.showMessage(UiMessages.NO_TASK_FOUND)

        val (newName, newDescription, newStateName) = readEditTaskInput()
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

        val newStateId = manageStateUseCase.getProjectStateIdByName(newStateName)
            ?: return printer.showMessage(UiMessages.INVALID_STATE_NAME)

        val userId = currentUser?.id
            ?: return printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

        val updatedTask = existingTask.copy(
            name = newName,
            description = newDescription,
            stateId = newStateId,
            updatedDate = DateHandlerImp().getCurrentDateTime()
        )

        manageTasksUseCase.editTask(updatedTask, userId).fold(
            onSuccess = { printer.printTask(updatedTask) },
            onFailure = ::handleFailure
        )
    }

    override fun deleteTask() {
        val taskName = getTaskName()

        if (manageTasksUseCase.getTaskByName(taskName).getOrNull() == null) {
            return printer.showMessage(UiMessages.NO_TASK_FOUND)
        }

        manageTasksUseCase.deleteTaskByName(taskName).fold(
            onSuccess = { printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY) },
            onFailure = ::handleFailure
        )
    }

    override fun showAllTasksInProject() {
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

    override fun showAllMateTaskAssignment() {
        val userName = getUserName()
        manageTasksInProjectUseCase.getAllTasksByUserName(userName).fold(
            onSuccess = { assignments -> printer.printMateTaskAssignments(assignments) },
            onFailure = ::handleFailure
        )
    }

    private fun readCreateTaskInput(): Triple<String, String, String>? {
        printer.showMessage(UiMessages.TASK_NAME_PROMPT)
        val name = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (name == null) {
            printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT)
            return null
        }

        printer.showMessage(UiMessages.TASK_DESCRIPTION_PROMPT)
        val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (description == null) {
            printer.showMessage(UiMessages.INVALID_DESCRIPTION_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readCreateTaskInput()
        }

        printer.showMessage(UiMessages.TASK_STATE_PROMPT)
        val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (stateName == null) {
            printer.showMessage(UiMessages.INVALID_STATE_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readCreateTaskInput()
        }
        return Triple(name, description, stateName)

    }

    private fun readEditTaskInput(): Triple<String, String, String>? {
        printer.showMessage(UiMessages.NEW_TASK_NAME_PROMPT)
        val name = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (name == null) {
            printer.showMessage(UiMessages.INVALID_TASK_NAME_INPUT_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readEditTaskInput()
        }

        printer.showMessage(UiMessages.TASK_DESCRIPTION_PROMPT)
        val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (description == null) {
            printer.showMessage(UiMessages.INVALID_DESCRIPTION_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readEditTaskInput()
        }

        printer.showMessage(UiMessages.TASK_STATE_PROMPT)
        val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (stateName == null) {
            printer.showMessage(UiMessages.INVALID_STATE_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readEditTaskInput()
        }

        return Triple(name, description, stateName)
    }

    private fun getProjectByName(): String {
        while (true) {
            printer.showMessage(UiMessages.PROJECT_NAME_PROMPT)
            val projectNameInput = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            if (projectNameInput == null) {
                printer.showMessage(UiMessages.EMPTY_PROJECT_NAME_INPUT)
                continue
            }

            val project = manageProjectUseCase.getProjectByName(projectNameInput).getOrNull()
            if (project != null) {
                return projectNameInput
            } else {
                printer.showMessage(UiMessages.NO_PROJECT_FOUNDED)
            }

            printer.showMessage(UiMessages.INVALID_PROJECT_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val userInput = reader.readStringOrNull()
            if (userInput.isNullOrBlank()) {
                return ""
            }
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
        printer.showMessage(throwable.message ?: UiMessages.NO_TASK_FOUND)
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