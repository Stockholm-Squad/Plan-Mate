package org.example.ui.features.project

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.EntityStateExceptions
import org.example.logic.ProjectExceptions
import org.example.logic.entities.EntityType
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.admin.AdminEntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class ProjectManagerUiImp(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val stateManagerUi: AdminEntityStateManagerUi,
    private val manageStatesUseCase: ManageEntityStatesUseCase,
    private val auditServicesUseCase: AuditServicesUseCase,
    private val loginUseCase: LoginUseCase,
    private val taskManagerUi: TaskManagerUi
) : ProjectManagerUi {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        outputPrinter.showMessage(throwable.message ?: "Unknown error")
    }

    override fun showAllProjects() = runBlocking(errorHandler) {
        getProjectsUseCase.getAllProjects().let { projects ->
            if (projects.isEmpty()) {
                outputPrinter.showMessage("No projects found")
            } else {
                projects.forEachIndexed { index, project ->
                    outputPrinter.showMessage("${index + 1}. ${project.name}")
                }
            }
        }
    }


    override fun showProjectByName() {
        var projectName: String?
        do {
            outputPrinter.showMessage("Enter project Name: ")
            projectName = inputReader.readStringOrNull()

            if (projectName.isNullOrBlank()) {
                outputPrinter.showMessage("Project name cannot be empty. Please try again.")
            }
        } while (projectName.isNullOrBlank())

        runBlocking(errorHandler) {
            try {
                getProjectsUseCase.getProjectByName(projectName).let { project ->
                    outputPrinter.showMessage("Project Details:")
                    outputPrinter.showMessage("Name: ${project.name}")
                    val stateName: String =
                        manageStatesUseCase.getEntityStateNameByStateId(project.stateId) ?: "not exist state"
                    outputPrinter.showMessage("State: $stateName")
                }
            } catch (e: Exception) {
                outputPrinter.showMessage("No Project with that name")
            }

        }
    }

    override fun addProject() {
        outputPrinter.showMessage("Enter project name or leave it blank to back: ")
        val projectName = inputReader.readStringOrNull() ?: run {
            return
        }
        outputPrinter.showMessage("Available states:")
        stateManagerUi.showAllStates()
        var stateName = ""
        while (true) {
            outputPrinter.showMessage("Enter state Name (or 'new' to create a new state) or leave it blank to back: ")
            when (val input = inputReader.readStringOrNull()) {
                "new" -> stateManagerUi.addState()
                null -> return
                else -> {
                    stateName = input
                    break
                }
            }
        }


        val userId = loginUseCase.getCurrentUser()?.id

        if (userId == null) {
            outputPrinter.showMessage(UiMessages.USER_NOT_LOGGED_IN)
            return
        }

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
                        outputPrinter.showMessage("Project added successfully")
                        do {
                            outputPrinter.showMessage("Would you like to add tasks to this project? (Y/N): ")
                            val userInput = inputReader.readStringOrNull()
                            if (userInput.equals("N", ignoreCase = true)) break
                            if (userInput.equals("Y", ignoreCase = true)) {
                                taskManagerUi.addTask(projectName)
                            }
                        }while (true)
                    }
                    else
                        outputPrinter.showMessage("Failed to add project")
                }
            } catch (e: ProjectExceptions) {
                outputPrinter.showMessage("Project name already exists")
            } catch (e: EntityStateExceptions) {
                outputPrinter.showMessage("No State with that name")
            }
        }
    }

    override fun updateProject() = runBlocking(errorHandler) {

        outputPrinter.showMessage("Enter project Name to update or leave it black to back: ")
        val projectName = inputReader.readStringOrNull() ?: return@runBlocking

        try {
            getProjectsUseCase.getProjectByName(projectName).let { project ->
                val projectStateName: String =
                    manageStatesUseCase.getEntityStateNameByStateId(project.stateId) ?: "not exist state"
                outputPrinter.showMessage("Enter new project name (leave blank to keep '${project.name}'): ")

                val newName = inputReader.readStringOrNull()

                outputPrinter.showMessage("Current state: $projectStateName")

                outputPrinter.showMessage("Available states:")

                stateManagerUi.showAllStates()

                outputPrinter.showMessage("Enter new state Name (leave blank to keep '${projectStateName}'): ")

                val newProjectStateName = inputReader.readStringOrNull()

                    ?: return@runBlocking outputPrinter.showMessage(UiMessages.USER_NOT_LOGGED_IN)

                if (newName != null || newProjectStateName != null)
                    manageProjectUseCase.updateProject(
                        project.id,
                        newName ?: projectName,
                        newProjectStateName ?: projectStateName,

                        ).let { success ->
                        if (success) {
                            auditServicesUseCase.addAuditForUpdateEntity(
                                entityType = EntityType.PROJECT,
                                existEntityName = project.name,
                                newEntityName = newName ?: projectName,
                                entityId = project.id,
                                newStateName = newProjectStateName
                            )
                            outputPrinter.showMessage("Project updated successfully")
                        } else {
                            outputPrinter.showMessage("Failed to update project")
                        }

                    }
                else outputPrinter.showMessage("project doesn't changed")
            }
        } catch (e: ProjectExceptions) {
            outputPrinter.showMessage(e.message)
        } catch (e: EntityStateExceptions) {
            outputPrinter.showMessage(e.message)
        }
    }

    override fun deleteProject() {
        outputPrinter.showMessage("Enter project Name to delete or leave it blank to back: ")
        val projectName = inputReader.readStringOrNull() ?: return

        runBlocking(errorHandler) {
            try {
                manageProjectUseCase.removeProjectByName(projectName).let { success ->

                    if (success) {
                        val project = getProjectsUseCase.getProjectByName(projectName)
                        auditServicesUseCase.addAuditForDeleteEntity(
                            entityType = EntityType.PROJECT,
                            entityName = projectName,
                            entityId = project.id,
                        )
                        outputPrinter.showMessage("Project deleted successfully")
                    } else {
                        outputPrinter.showMessage("Failed to delete project")
                    }

                }
            } catch (e: ProjectExceptions) {
                outputPrinter.showMessage(e.message)
            }
        }
    }

    override fun launchUi() {

        if (loginUseCase.getCurrentUser() == null) {
            outputPrinter.showMessage(UiMessages.INVALID_USER)
            return
        }

        while (true) {
            outputPrinter.showMessage("\nProject Management:")
            outputPrinter.showMessage("1. Show all projects")
            outputPrinter.showMessage("2. Show project details")
            outputPrinter.showMessage("3. Add project")
            outputPrinter.showMessage("4. Update project")
            outputPrinter.showMessage("5. Delete project")
            outputPrinter.showMessage("0. Back")
            outputPrinter.showMessage("Enter your choice: ")

            when (inputReader.readStringOrNull()) {
                "1" -> showAllProjects()
                "2" -> showProjectByName()
                "3" -> addProject()
                "4" -> updateProject()
                "5" -> deleteProject()
                "0" -> return
                else -> outputPrinter.showMessage("Invalid choice")
            }
        }
    }
}