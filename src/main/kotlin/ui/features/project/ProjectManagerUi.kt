package org.example.ui.features.project

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.EntityType
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class ProjectManagerUi(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val adminStateManagerUi: AdminEntityStateManagerUi,
    private val showAllEntityStateManagerUi: ShowAllEntityStateManagerUi,
    private val manageStatesUseCase: ManageEntityStatesUseCase,
    private val auditServicesUseCase: AuditServicesUseCase,
    private val loginUseCase: LoginUseCase,
    private val taskManagerUi: TaskManagerUi,
) : UiLauncher {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        outputPrinter.showMessageLine(throwable.message ?: UiMessages.UNKNOWN_ERROR)
    }

    override fun launchUi() {

        if (loginUseCase.getCurrentUser() == null) {
            outputPrinter.showMessageLine(UiMessages.INVALID_USER)
            return
        }

        while (true) {
            outputPrinter.showMessageLine(UiMessages.SHOW_PROJECT_MANAGEMENT_OPTIONS)
            outputPrinter.showMessage(UiMessages.SELECT_OPTION)

            when (inputReader.readStringOrNull()) {
                "1" -> showAllProjects()
                "2" -> showProjectByName()
                "3" -> addProject()
                "4" -> updateProject()
                "5" -> deleteProject()
                "0" -> break
                else -> outputPrinter.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE)
            }
        }
    }

    private fun showAllProjects() = runBlocking(errorHandler) {
        try {
            getProjectsUseCase.getAllProjects().let { projects ->
                if (projects.isEmpty()) {
                    outputPrinter.showMessageLine(UiMessages.NO_PROJECTS_FOUND)
                } else {
                    projects.forEachIndexed { index, project ->
                        outputPrinter.showMessageLine("${index + 1}. ${project.title}")
                    }
                }
            }
        } catch (exception: Exception) {
            outputPrinter.showMessageLine(exception.message ?: UiMessages.UNKNOWN_ERROR)
        }
    }


    private fun showProjectByName() {
        var projectName: String?
        do {
            outputPrinter.showMessage(UiMessages.PROJECT_NAME_PROMPT)
            projectName = inputReader.readStringOrNull()

            if (projectName.isNullOrBlank()) {
                outputPrinter.showMessageLine("${UiMessages.PROJECT_CAN_NOT_BE_EMPTY} ${UiMessages.PLEASE_TRY_AGAIN}")
            }
        } while (projectName.isNullOrBlank())

        runBlocking(errorHandler) {
            try {
                getProjectsUseCase.getProjectByName(projectName).let { project ->
                    outputPrinter.showMessageLine(UiMessages.PROJECT_DETAILS)
                    outputPrinter.showMessageLine("${UiMessages.NAME} ${project.title}")
                    val stateName: String = manageStatesUseCase.getEntityStateNameByStateId(project.stateId)
                    outputPrinter.showMessageLine("${UiMessages.STATE} $stateName")
                }
            } catch (e: Exception) {
                outputPrinter.showMessageLine(e.message ?: UiMessages.UNKNOWN_ERROR)
            }

        }
    }

    fun addProject() {
        outputPrinter.showMessage("${UiMessages.PROJECT_NAME_PROMPT} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
        val projectName = inputReader.readStringOrNull() ?: run {
            return
        }
        val stateName = enterStateForProject()

        runBlocking(errorHandler) {
            try {
                manageProjectUseCase.addProject(projectName, stateName).let { success ->
                    if (success) {
                        val project = getProjectsUseCase.getProjectByName(projectName)
                        auditServicesUseCase.addAuditForAddEntity(
                            entityType = EntityType.PROJECT,
                            entityName = projectName,
                            entityId = project.id,
                            additionalInfo = stateName
                        )
                        outputPrinter.showMessageLine(UiMessages.PROJECT_ADDED)
                        do {
                            outputPrinter.showMessageLine(UiMessages.YOU_LIKE_TO_ADD_TASKS)
                            val userInput = inputReader.readStringOrNull()
                            if (userInput.equals(UiMessages.N, ignoreCase = true)) break
                            if (userInput.equals(UiMessages.Y, ignoreCase = true)) {
                                taskManagerUi.addTask(projectName)
                            }
                        } while (true)
                    } else
                        outputPrinter.showMessageLine(UiMessages.FAILED_TO_ADD_PROJECT)
                }
            } catch (e: Exception) {
                outputPrinter.showMessageLine("${UiMessages.FAILED_TO_ADD_PROJECT} ${e.message}")
            }
        }
    }

    private fun enterStateForProject(): String {
        outputPrinter.showMessageLine(UiMessages.AVAILABLE_STATE)
        showAllEntityStateManagerUi.launchUi()
        while (true) {
            outputPrinter.showMessage("${UiMessages.ENTER_STATE_NAME_OR_NEW_TO_CREATE} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
            when (val stateName = inputReader.readStringOrNull()) {
                UiMessages.NEW -> addState()
                null -> {
                    outputPrinter.showMessageLine(UiMessages.INVALID_INPUT)
                }

                else -> {
                    return stateName
                }
            }
        }
    }

    private fun addState() {
        return try {
            adminStateManagerUi.addState()
        } catch (exception: Exception) {
            outputPrinter.showMessageLine(exception.message ?: UiMessages.UNKNOWN_ERROR)
        }
    }

    fun updateProject() = runBlocking(errorHandler) {

        outputPrinter.showMessage("${UiMessages.ENTER_PROJECT_NAME_TO_UPDATE} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
        val projectName = inputReader.readStringOrNull() ?: return@runBlocking

        try {
            getProjectsUseCase.getProjectByName(projectName).let { project ->
                val projectStateName: String =
                    manageStatesUseCase.getEntityStateNameByStateId(project.stateId)
                outputPrinter.showMessage("${UiMessages.ENTER_NEW_PROJECT_NAME} (${UiMessages.LEAVE_BLANK_TO_KEEP} '${project.title}'): ")
                val newName = inputReader.readStringOrNull()

                outputPrinter.showMessageLine("${UiMessages.CURRENT_STATE} $projectStateName")
                outputPrinter.showMessageLine(UiMessages.AVAILABLE_STATE)
                showAllEntityStateManagerUi.launchUi()
                outputPrinter.showMessageLine("${UiMessages.ENTER_NEW_STATE} (${UiMessages.LEAVE_BLANK_TO_KEEP} '${projectStateName}'): ")
                val newProjectStateName = inputReader.readStringOrNull()
                    ?: return@runBlocking outputPrinter.showMessageLine(UiMessages.INVALID_INPUT)

                if (newName != null) {
                    manageProjectUseCase.updateProject(
                        project.id,
                        newName,
                        newProjectStateName,

                        ).let { success ->
                        if (success) {
                            auditServicesUseCase.addAuditForUpdateEntity(
                                entityType = EntityType.PROJECT,
                                existEntityName = project.title,
                                newEntityName = newName,
                                entityId = project.id,
                                newStateName = newProjectStateName
                            )
                            outputPrinter.showMessageLine(UiMessages.PROJECT_UPDATED)
                        } else {
                            outputPrinter.showMessageLine(UiMessages.FAILED_TO_UPDATE_PROJECT)
                        }

                    }
                } else outputPrinter.showMessageLine(UiMessages.PROJECT_DOES_NOT_EXIST)
            }
        } catch (e: Exception) {
            outputPrinter.showMessageLine("${UiMessages.FAILED_TO_UPDATE_PROJECT} ${e.message}")
        }
    }

    fun deleteProject() {
        outputPrinter.showMessageLine("${UiMessages.ENTER_PROJECT_NAME_TO_DELETE} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
        val projectName = inputReader.readStringOrNull() ?: return

        runBlocking(errorHandler) {
            try {
                val project = getProjectsUseCase.getProjectByName(projectName)
                manageProjectUseCase.removeProjectByName(projectName).let { success ->
                    if (success) {
                        auditServicesUseCase.addAuditForDeleteEntity(
                            entityType = EntityType.PROJECT,
                            entityName = projectName,
                            entityId = project.id,
                        )
                        outputPrinter.showMessageLine(UiMessages.PROJECT_DELETED)
                    } else {
                        outputPrinter.showMessageLine(UiMessages.FAILED_TO_DELETE_PROJECT)
                    }
                }
            } catch (e: Exception) {
                outputPrinter.showMessageLine("${UiMessages.FAILED_TO_DELETE_PROJECT} ${e.message}")
            }
        }
    }
}