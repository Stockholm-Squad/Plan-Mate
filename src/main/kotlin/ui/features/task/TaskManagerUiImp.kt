package org.example.ui.features.task

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.EntityType
import org.example.logic.entities.Task
import org.example.logic.entities.User
import org.example.logic.usecase.audit.AddAuditSystemUseCase
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
    private val auditSystemUseCase: AddAuditSystemUseCase
) : TaskManagerUi {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printer.showMessage(throwable.message ?: "Unknown error")
    }
    val timestamp = DateHandlerImp().getCurrentDateTime()
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

    override fun showAllTasks() {
        runBlocking(coroutineExceptionHandler) {
            try {
                manageTasksUseCase.getAllTasks().also {
                    printer.printTaskList(it)
                }
            } catch (ex: Exception) {
                printer.showMessage(ex.message ?: "Unknown Error")
            }
        }
    }

    override fun createTask() {
        val projectName = getProjectByName()
        if (projectName.isEmpty()) return

        val (name, description, stateName) = readCreateTaskInput()
            ?: return printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

        val stateId = runBlocking(coroutineExceptionHandler) {
            try {
                manageStateUseCase.getProjectStateIdByName(stateName)
            } catch (ex: Exception) {
                printer.showMessage(ex.message ?: "Unknown Error")
                null
            }
        } ?: return

        val userId = currentUser?.id ?: return printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

        runBlocking(coroutineExceptionHandler) {
            try {
                val project = getProjectsUseCase.getProjectByName(projectName)
                val task = Task(
                    projectName = projectName,
                    name = name,
                    description = description,
                    stateId = stateId,
                    createdDate = timestamp,
                    updatedDate = timestamp
                )

                manageTasksUseCase.addTask(task, project.id)
                manageTasksInProjectUseCase.addTaskToProject(project.id, task.id)
                val taskDescription = printer.printAddTaskDescription(EntityType.TASK, task.name, task.id, projectName)
                auditSystemUseCase.addEntityChangeHistory(userId, EntityType.TASK, task.id, taskDescription, timestamp)
                printer.printTask(task)
            } catch (ex: Exception) {
                printer.showMessage(ex.message ?: "Unknown Error")
            }
        }
    }

    override fun editTask() = runBlocking(coroutineExceptionHandler) {
        try {
            val taskName = getTaskName()

            val existingTask = manageTasksUseCase.getTaskByName(taskName)

            val editInput = readEditTaskInput()
                ?: return@runBlocking printer.showMessage(UiMessages.EMPTY_TASK_INPUT)

            val (newName, newDescription, newStateName) = editInput

            val newStateId = manageStateUseCase.getProjectStateIdByName(newStateName)

            val userId = currentUser?.id
                ?: return@runBlocking printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

            val updatedTask = existingTask.copy(
                name = newName,
                description = newDescription,
                stateId = newStateId,
                updatedDate = timestamp
            )

            manageTasksUseCase.editTask(updatedTask)
            val taskDescription =
                printer.printUpdateTaskDescription(EntityType.TASK, existingTask.name, newDescription, newStateName)
            logAuditUseCase.logAudit(userId, EntityType.TASK, existingTask.id, taskDescription, timestamp)
            printer.printTask(updatedTask)
        } catch (ex: Exception) {
            printer.showMessage(ex.message ?: "Unknown Error.")
        }
    }

    override fun deleteTask() = runBlocking(coroutineExceptionHandler) {
        try {
            val projectName = getProjectByName()
            if (projectName.isEmpty()) return@runBlocking

            val taskName = getTaskName()

            val task = manageTasksUseCase.getTaskByName(taskName)
            manageTasksUseCase.deleteTaskByName(taskName)

            val project = getProjectsUseCase.getProjectByName(projectName)
            manageTasksInProjectUseCase.deleteTaskFromProject(project.id, task.id)
            val userId = currentUser?.id
                ?: return@runBlocking printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)

            val description =
                "Deleted -> ${currentUser?.username} delete ${EntityType.PROJECT} ${project.name} (${project.id}) at $timestamp"
            auditSystemUseCase.addEntityChangeHistory(userId, EntityType.TASK, project.id, description)
            printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY)
        } catch (ex: Exception) {
            printer.showMessage(ex.message ?: "Unknown Error")
        }
    }

    override fun showAllTasksInProject(): List<Task> = runBlocking(coroutineExceptionHandler) {
        try {
            val projectName = getProjectByName()
            val tasks = manageTasksInProjectUseCase.getTasksInProjectByName(projectName)
            printer.printTaskList(tasks)
            tasks
        } catch (ex: Exception) {
            printer.showMessage(ex.message ?: "Unknown Error")
            emptyList()
        }
    }

    override fun showAllMateTaskAssignment() = runBlocking(coroutineExceptionHandler) {
        try {
            val userName = getUserName()
            val assignments = manageTasksInProjectUseCase.getAllTasksByUserName(userName)
            printer.printMateTaskAssignments(assignments)
        } catch (ex: Exception) {
            printer.showMessage(ex.message ?: "Unknown Error")
        }
    }

    override fun getTaskByName() = runBlocking(coroutineExceptionHandler) {
        try {
            val taskName = getTaskName()
            val task = manageTasksUseCase.getTaskByName(taskName)
            printer.printTask(task)
        } catch (ex: Exception) {
            printer.showMessage(ex.message ?: "Unknown Error")
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
            if (confirm.equals("y", ignoreCase = true)) return readEditTaskInput()
            return null
        }

        printer.showMessage(UiMessages.TASK_DESCRIPTION_PROMPT)
        val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (description == null) {
            printer.showMessage(UiMessages.INVALID_DESCRIPTION_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.equals("y", ignoreCase = true)) return readEditTaskInput()
            return null
        }

        printer.showMessage(UiMessages.TASK_STATE_PROMPT)
        val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (stateName == null) {
            printer.showMessage(UiMessages.INVALID_STATE_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.equals("y", ignoreCase = true)) return readEditTaskInput()
            return null
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

            val isValidProject = runBlocking {
                try {
                    getProjectsUseCase.getProjectByName(projectNameInput)
                    true
                } catch (ex: Exception) {
                    printer.showMessage(ex.message ?: "Unknown Error")
                    false
                }
            }

            if (isValidProject) {
                return projectNameInput
            }

            printer.showMessage(UiMessages.INVALID_PROJECT_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val userInput = reader.readStringOrNull()
            if (userInput.isNullOrBlank()) {
                return ""
            } else if (userInput.equals("y", ignoreCase = true)) {
                continue
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