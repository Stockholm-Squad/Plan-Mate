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
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class ProjectManagerUiImp(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val adminStateManagerUi: AdminEntityStateManagerUi,
    private val showAllEntityStateManagerUi: ShowAllEntityStateManagerUi,
    private val manageStatesUseCase: ManageEntityStatesUseCase,
    private val auditServicesUseCase: AuditServicesUseCase,
    private val loginUseCase: LoginUseCase,
    private val taskManagerUi: TaskManagerUi
) : ProjectManagerUi {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        outputPrinter.showMessageLine(throwable.message ?: "Unknown error")
    }

    override fun showAllProjects() = runBlocking(errorHandler) {
        getProjectsUseCase.getAllProjects().let { projects ->
            if (projects.isEmpty()) {
                outputPrinter.showMessageLine("No projects found")
            } else {
                projects.forEachIndexed { index, project ->
                    outputPrinter.showMessageLine("${index + 1}. ${project.title}")
                }
            }
        }
    }


    override fun showProjectByName() {
        var projectName: String?
        do {
            outputPrinter.showMessageLine("Enter project Name: ")
            projectName = inputReader.readStringOrNull()

            if (projectName.isNullOrBlank()) {
                outputPrinter.showMessageLine("Project name cannot be empty. Please try again.")
            }
        } while (projectName.isNullOrBlank())

        runBlocking(errorHandler) {
            try {
                getProjectsUseCase.getProjectByName(projectName).let { project ->
                    outputPrinter.showMessageLine("Project Details:")
                    outputPrinter.showMessageLine("Name: ${project.title}")
                    val stateName: String =
                        manageStatesUseCase.getEntityStateNameByStateId(project.stateId) ?: "not exist state"
                    outputPrinter.showMessageLine("State: $stateName")
                }
            } catch (e: Exception) {
                outputPrinter.showMessageLine("No Project with that name")
            }

        }
    }

    override fun addProject() {
        outputPrinter.showMessageLine("Enter project name or leave it blank to back: ")
        val projectName = inputReader.readStringOrNull() ?: run {
            return
        }
        outputPrinter.showMessageLine("Available states:")
        showAllEntityStateManagerUi.launchUi()
        var stateName = ""
        while (true) {
            outputPrinter.showMessageLine("Enter state Name (or 'new' to create a new state) or leave it blank to back: ")
            when (val input = inputReader.readStringOrNull()) {
                "new" -> adminStateManagerUi.addState()
                null -> return
                else -> {
                    stateName = input
                    break
                }
            }
        }


        val userId = loginUseCase.getCurrentUser()?.id

        if (userId == null) {
            outputPrinter.showMessageLine(UiMessages.USER_NOT_LOGGED_IN)
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
                        outputPrinter.showMessageLine("Project added successfully")
                        do {
                            outputPrinter.showMessageLine("Would you like to add tasks to this project? (Y/N): ")
                            val userInput = inputReader.readStringOrNull()
                            if (userInput.equals("N", ignoreCase = true)) break
                            if (userInput.equals("Y", ignoreCase = true)) {
                                taskManagerUi.addTask(projectName)
                            }
                        }while (true)
                    }
                    else
                        outputPrinter.showMessageLine("Failed to add project")
                }
            } catch (e: ProjectExceptions) {
                outputPrinter.showMessageLine("Project name already exists")
            } catch (e: EntityStateExceptions) {
                outputPrinter.showMessageLine("No State with that name")
            }
        }
    }

    override fun updateProject() = runBlocking(errorHandler) {

        outputPrinter.showMessageLine("Enter project Name to update or leave it black to back: ")
        val projectName = inputReader.readStringOrNull() ?: return@runBlocking

        try {
            getProjectsUseCase.getProjectByName(projectName).let { project ->
                val projectStateName: String =
                    manageStatesUseCase.getEntityStateNameByStateId(project.stateId) ?: "not exist state"
                outputPrinter.showMessageLine("Enter new project name (leave blank to keep '${project.title}'): ")

                val newName = inputReader.readStringOrNull()

                outputPrinter.showMessageLine("Current state: $projectStateName")

                outputPrinter.showMessageLine("Available states:")

                showAllEntityStateManagerUi.launchUi()

                outputPrinter.showMessageLine("Enter new state Name (leave blank to keep '${projectStateName}'): ")

                val newProjectStateName = inputReader.readStringOrNull()

                    ?: return@runBlocking outputPrinter.showMessageLine(UiMessages.USER_NOT_LOGGED_IN)

                if (newName != null || newProjectStateName != null)
                    manageProjectUseCase.updateProject(
                        project.id,
                        newName ?: projectName,
                        newProjectStateName ?: projectStateName,

                        ).let { success ->
                        if (success) {
                            auditServicesUseCase.addAuditForUpdateEntity(
                                entityType = EntityType.PROJECT,
                                existEntityName = project.title,
                                newEntityName = newName ?: projectName,
                                entityId = project.id,
                                newStateName = newProjectStateName
                            )
                            outputPrinter.showMessageLine("Project updated successfully")
                        } else {
                            outputPrinter.showMessageLine("Failed to update project")
                        }

                    }
                else outputPrinter.showMessageLine("project doesn't changed")
            }
        } catch (e: ProjectExceptions) {
            outputPrinter.showMessageLine(e.message)
        } catch (e: EntityStateExceptions) {
            outputPrinter.showMessageLine(e.message)
        }
    }

    override fun deleteProject() {
        outputPrinter.showMessageLine("Enter project Name to delete or leave it blank to back: ")
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
                        outputPrinter.showMessageLine("Project deleted successfully")
                    } else {
                        outputPrinter.showMessageLine("Failed to delete project")
                    }

                }
            } catch (e: ProjectExceptions) {
                outputPrinter.showMessageLine(e.message)
            }
        }
    }

    override fun launchUi() {

        if (loginUseCase.getCurrentUser() == null) {
            outputPrinter.showMessageLine(UiMessages.INVALID_USER)
            return
        }

        while (true) {
            outputPrinter.showMessageLine("\nProject Management:")
            outputPrinter.showMessageLine("1. Show all projects")
            outputPrinter.showMessageLine("2. Show project details")
            outputPrinter.showMessageLine("3. Add project")
            outputPrinter.showMessageLine("4. Update project")
            outputPrinter.showMessageLine("5. Delete project")
            outputPrinter.showMessageLine("0. Back")
            outputPrinter.showMessageLine("Enter your choice: ")

            when (inputReader.readStringOrNull()) {
                "1" -> showAllProjects()
                "2" -> showProjectByName()
                "3" -> addProject()
                "4" -> updateProject()
                "5" -> deleteProject()
                "0" -> return
                else -> outputPrinter.showMessageLine("Invalid choice")
            }
        }
    }
}