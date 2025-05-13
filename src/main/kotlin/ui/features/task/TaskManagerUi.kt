package org.example.ui.features.task

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.EntityType
import org.example.logic.entities.Task
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.logic.utils.DateHandlerImp
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.common.utils.UiUtils
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class TaskManagerUi(
    private val reader: InputReader,
    private val printer: OutputPrinter,
    private val uiUtils: UiUtils,
    private val manageTasksUseCase: ManageTasksUseCase,
    private val manageStateUseCase: ManageEntityStatesUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val manageTasksInProjectUseCase: ManageTasksInProjectUseCase,
    private val auditServicesUseCase: AuditServicesUseCase,
    private val loginUseCase: LoginUseCase,
) : UiLauncher {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printer.showMessageLine(throwable.message ?: UiMessages.UNKNOWN_ERROR)
    }
    val timestamp = DateHandlerImp().getCurrentDateTime()

    override fun launchUi() {

        if (loginUseCase.getCurrentUser() == null) {
            printer.showMessageLine(UiMessages.INVALID_USER)
            return
        }

        while (true) {
            printer.showMessageLine(UiMessages.SHOW_TASK_MANAGEMENT_OPTIONS)
            printer.showMessage(UiMessages.SELECT_OPTION)
            if (enteredTaskOption(reader.readIntOrNull())) break
        }
    }

    private fun enteredTaskOption(option: Int?): Boolean {
        when (option) {
            1 -> showAllTasks()
            2 -> getTaskByName()
            3 -> addTask(null)
            4 -> updateTask()
            5 -> deleteTask()
            6 -> showAllTasksInProject()
            7 -> showAllMateTaskAssignment()
            8 -> {
                printer.showMessageLine(UiMessages.GOODBYE)
                return true
            }

            else -> printer.showMessageLine(UiMessages.INVALID_OPTION)
        }
        return false
    }

    fun showAllTasks() {
        runBlocking(coroutineExceptionHandler) {
            try {
                manageTasksUseCase.getAllTasks().also {
                    printer.printTaskList(it)
                }
            } catch (ex: Exception) {
                printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
            }
        }
    }

    fun addTask(passedProjectName: String?) {
        val projectName = passedProjectName ?: getProjectByName()
        if (projectName.isEmpty()) return

        val (name, description, stateName) = readCreateTaskInput()
            ?: return printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT)

        val stateId = runBlocking(coroutineExceptionHandler) {
            try {
                manageStateUseCase.getEntityStateIdByName(stateName)
            } catch (ex: Exception) {
                printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
                null
            }
        } ?: return

        runBlocking(coroutineExceptionHandler) {
            try {
                val project = getProjectsUseCase.getProjectByName(projectName)

                val task = Task(
                    projectTitle = projectName,
                    title = name,
                    description = description,
                    stateId = stateId,
                    createdDate = timestamp,
                    updatedDate = timestamp
                )
                manageTasksUseCase.addTask(task, project.id)
                manageTasksInProjectUseCase.addTaskToProject(project.id, task.id)
                auditServicesUseCase.addAuditForAddEntity(
                    EntityType.TASK, task.title,
                    entityId = task.id,
                    additionalInfo = projectName
                )
                printer.printTask(task)
            } catch (ex: Exception) {
                printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
            }
        }
    }

    fun updateTask() = runBlocking(coroutineExceptionHandler) {
        try {
            val projectName = getProjectByName()
            if (projectName.isEmpty()) return@runBlocking

            val taskName = getTaskName()

            val existingTask = manageTasksUseCase.getTaskByName(taskName)

            val updateInput = readUpdateTaskInput()
                ?: return@runBlocking printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT)

            val (newName, newDescription, newStateName) = updateInput

            val newStateId = manageStateUseCase.getEntityStateIdByName(newStateName)
            val updatedTask = existingTask.copy(
                title = newName,
                description = newDescription,
                stateId = newStateId,
                updatedDate = timestamp
            )
            getProjectsUseCase.getProjectByName(projectName)

            manageTasksUseCase.updateTask(updatedTask)
            auditServicesUseCase.addAuditForUpdateEntity(
                entityType = EntityType.TASK,
                existEntityName = existingTask.title,
                newEntityName = updatedTask.title,
                entityId = existingTask.id,
                newDescription = newDescription,
                newStateName = newStateName,
                additionalInfo = projectName
            )
            printer.printTask(updatedTask)
        } catch (ex: Exception) {
            printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
        }
    }

    fun deleteTask() = runBlocking(coroutineExceptionHandler) {
        try {
            val projectName = getProjectByName()
            if (projectName.isEmpty()) return@runBlocking

            val taskName = getTaskName()

            val task = manageTasksUseCase.getTaskByName(taskName)
            manageTasksUseCase.deleteTaskByName(taskName)

            val project = getProjectsUseCase.getProjectByName(projectName)
            manageTasksInProjectUseCase.deleteTaskFromProject(project.id, task.id)
            auditServicesUseCase.addAuditForDeleteEntity(
                entityType = EntityType.TASK,
                entityName = taskName,
                entityId = task.id,
                additionalInfo = projectName
            )
            printer.showMessageLine(UiMessages.TASK_DELETE_SUCCESSFULLY)
        } catch (ex: Exception) {
            printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
        }
    }

    fun showAllTasksInProject(): List<Task> = runBlocking(coroutineExceptionHandler) {
        try {
            val projectName = getProjectByName()
            val tasks = manageTasksInProjectUseCase.getTasksInProjectByName(projectName)
            printer.printTaskList(tasks)
            tasks
        } catch (ex: Exception) {
            printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
            emptyList()
        }
    }

    private fun showAllMateTaskAssignment() = runBlocking(coroutineExceptionHandler) {
        try {
            val userName = getUserName()
            val assignments = manageTasksInProjectUseCase.getAllTasksByUserName(userName)
            printer.printMateTaskAssignments(assignments)
        } catch (ex: Exception) {
            printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
        }
    }

    private fun getTaskByName() = runBlocking(coroutineExceptionHandler) {
        try {
            val taskName = getTaskName()
            val task = manageTasksUseCase.getTaskByName(taskName)
            printer.printTask(task)
        } catch (ex: Exception) {
            printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
        }
    }

    private fun readCreateTaskInput(): Triple<String, String, String>? {
        printer.showMessage(UiMessages.TASK_NAME_PROMPT)
        val name = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (name == null) {
            printer.showMessageLine(UiMessages.EMPTY_TASK_NAME_INPUT)
            return null
        }

        printer.showMessage(UiMessages.TASK_DESCRIPTION_PROMPT)
        val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (description == null) {
            printer.showMessageLine(UiMessages.INVALID_DESCRIPTION_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readCreateTaskInput()
        }

        printer.showMessage(UiMessages.TASK_STATE_PROMPT)
        val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (stateName == null) {
            printer.showMessageLine(UiMessages.INVALID_STATE_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.isNullOrBlank()) return null
            return readCreateTaskInput()
        }
        return Triple(name, description, stateName)

    }

    private fun readUpdateTaskInput(): Triple<String, String, String>? {
        printer.showMessageLine(UiMessages.NEW_TASK_NAME_PROMPT)
        val name = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (name == null) {
            printer.showMessageLine(UiMessages.INVALID_TASK_NAME_INPUT_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.equals("Y", ignoreCase = true)) return readUpdateTaskInput()
            return null
        }

        printer.showMessageLine(UiMessages.TASK_DESCRIPTION_PROMPT)
        val description = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (description == null) {
            printer.showMessageLine(UiMessages.INVALID_DESCRIPTION_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.equals("Y", ignoreCase = true)) return readUpdateTaskInput()
            return null
        }

        printer.showMessageLine(UiMessages.TASK_STATE_PROMPT)
        val stateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
        if (stateName == null) {
            printer.showMessageLine(UiMessages.INVALID_STATE_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val confirm = reader.readStringOrNull()
            if (confirm.equals("Y", ignoreCase = true)) return readUpdateTaskInput()
            return null
        }

        return Triple(name, description, stateName)
    }

    private fun getProjectByName(): String {
        while (true) {
            printer.showMessageLine(UiMessages.PROJECT_NAME_PROMPT)
            val projectNameInput = reader.readStringOrNull()?.takeIf { it.isNotBlank() }

            if (projectNameInput == null) {
                printer.showMessageLine(UiMessages.EMPTY_PROJECT_NAME_INPUT)
                continue
            }

            val isValidProject = runBlocking {
                try {
                    getProjectsUseCase.getProjectByName(projectNameInput)
                    true
                } catch (ex: Exception) {
                    printer.showMessageLine(ex.message ?: UiMessages.UNKNOWN_ERROR)
                    false
                }
            }

            if (isValidProject) {
                return projectNameInput
            }

            printer.showMessageLine(UiMessages.INVALID_PROJECT_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU)
            val userInput = reader.readStringOrNull()
            if (userInput.isNullOrBlank()) {
                return ""
            } else if (userInput.equals(UiMessages.Y, ignoreCase = true)) {
                continue
            }
        }
    }

    private fun getTaskName(): String {
        while (true) {
            printer.showMessageLine(UiMessages.TASK_NAME_PROMPT)
            val taskName = uiUtils.readNonBlankInputOrNull(reader)
            if (taskName == null) {
                printer.showMessageLine(UiMessages.EMPTY_TASK_NAME_INPUT)
                continue
            }
            return taskName
        }
    }

    private fun getUserName(): String {
        while (true) {
            printer.showMessageLine(UiMessages.USER_NAME_PROMPT)
            val userName = uiUtils.readNonBlankInputOrNull(reader)
            if (userName == null) {
                printer.showMessageLine(UiMessages.EMPTY_USER_NAME_INPUT)
                continue
            }
            return userName
        }
    }
}