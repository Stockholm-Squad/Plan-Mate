package org.example.ui.features.task

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logic.models.entities.Task
import logic.models.entities.User
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.project.GetProjectsUseCase
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
    private val getProjectsUseCase: GetProjectsUseCase,
    private val manageTasksInProjectUseCase: ManageTasksInProjectUseCase,
) : TaskManagerUi {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printer.showMessage(throwable.message ?: "Unknown error")
    }

    private var currentUser: User? = null

    override suspend fun launchUi(user: User?) {
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

    private suspend fun enteredTaskOption(option: TaskOptions?): Boolean {
        when (option) {
            TaskOptions.SHOW_ALL_TASKS -> showAllTasks()
            TaskOptions.SHOW_TASK_BY_NAME -> getTaskByName()
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

    override suspend fun showAllTasks() {
        val tasks = withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksUseCase.getAllTasks()
        }
        withContext(Dispatchers.Main) {
            tasks.takeIf { it.isNotEmpty() }?.let { printer.printTaskList(it) }
                ?: printer.showMessage(UiMessages.NO_TASKS_FOUND)
        }
    }


    override suspend fun createTask() {
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

        val userId = currentUser?.id ?: return printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

        val project = getProjectsUseCase.getProjectByName(projectName)

        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksUseCase.addTask(task, userId)

            manageTasksInProjectUseCase.addTaskToProject(project.id, task.id)
        }
        withContext(Dispatchers.Main) {
            printer.printTask(task)
        }
    }

    override suspend fun editTask() {
        val taskName = getTaskName()

        val existingTask =
            manageTasksUseCase.getTaskByName(taskName) ?: return printer.showMessage(UiMessages.NO_TASK_FOUND)

        val (newName, newDescription, newStateName) = readEditTaskInput()
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

        val newStateId = manageStateUseCase.getProjectStateIdByName(newStateName) ?: return printer.showMessage(
            UiMessages.INVALID_STATE_NAME
        )

        val userId = currentUser?.id ?: return printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

        val updatedTask = existingTask.copy(
            name = newName,
            description = newDescription,
            stateId = newStateId,
            updatedDate = DateHandlerImp().getCurrentDateTime()
        )
        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksUseCase.editTask(updatedTask, userId)
        }

        withContext(Dispatchers.Main) {
            printer.printTask(updatedTask)
        }
    }

    override suspend fun deleteTask() {
        val taskName = getTaskName()
        val existingTask = manageTasksUseCase.getTaskByName(taskName)
        if (existingTask == null) return printer.showMessage(UiMessages.NO_TASK_FOUND)

        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksUseCase.deleteTaskByName(taskName)
        }
        withContext(Dispatchers.Main) {
            printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY)
        }
    }

    override suspend fun showAllTasksInProject() {
        val projectName = getProjectByName()
        val tasks = withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksInProjectUseCase.getTasksInProjectByName(projectName)
        }
        withContext(Dispatchers.Main) {
            tasks.takeIf { it.isNotEmpty() }?.let { printer.printTaskList(it) }
                ?: printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT)
        }
    }

    override suspend fun showAllMateTaskAssignment() {
        val userName = getUserName()
        val assignments = withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksInProjectUseCase.getAllTasksByUserName(userName)
        }
        withContext(Dispatchers.Main) {
            assignments.takeIf { it.isNotEmpty() }?.let { printer.printMateTaskAssignments(assignments) }
                ?: printer.showMessage(UiMessages.NO_TASK_FOUND)
        }
    }

    override suspend fun getTaskByName() {
        val taskName = getTaskName()
        val task = withContext(Dispatchers.IO + coroutineExceptionHandler) {
            manageTasksUseCase.getTaskByName(taskName)
        }
        withContext(Dispatchers.Main) {
            task.let { printer.printTask(it) } ?: printer.showMessage(UiMessages.NO_TASK_FOUND)
        }
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

    private suspend fun getProjectByName(): String {
        while (true) {
            printer.showMessage(UiMessages.PROJECT_NAME_PROMPT)
            val projectNameInput = reader.readStringOrNull()?.takeIf { it.isNotBlank() }

            if (projectNameInput == null) {
                printer.showMessage(UiMessages.EMPTY_PROJECT_NAME_INPUT)
                continue
            }

            val project = withContext(Dispatchers.IO + coroutineExceptionHandler) {
                runCatching {
                    getProjectsUseCase.getProjectByName(projectNameInput)
                }.getOrNull()
            }

            if (project != null) {
                return projectNameInput
            } else {
                printer.showMessage(UiMessages.NO_PROJECT_FOUND)
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

    private fun getEnteredOption(option: Int?) = TaskOptions.entries.find { it.option == option }


    private fun printTaskOptionsMenu() {
        printer.showMessage("========================= Tasks Option =========================")
        printer.showMessage("Please Choose an option. Pick a number between 0 and 7!\n")

        TaskOptions.entries.forEach { println("${it.option}. ${it.label}") }

        printer.showMessage("-----------------------------------------------------")
        printer.showMessage("Choose an option: ")
    }
}