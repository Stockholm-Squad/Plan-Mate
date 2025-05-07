package org.example.ui.features.project

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logic.models.entities.User
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class ProjectManagerUiImp(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val stateManagerUi: AdminStateManagerUi,
    private val manageStatesUseCase: ManageStatesUseCase,
) : ProjectManagerUi {
    private var currentUser: User? = null

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        outputPrinter.showMessage(throwable.message ?: "Unknown error")
    }

    override fun showAllProjects() {

        CoroutineScope(Dispatchers.IO).launch(errorHandler) {
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

        CoroutineScope(Dispatchers.IO).launch(errorHandler) {
            withContext(Dispatchers.Main) {
                getProjectsUseCase.getProjectByName(projectName).let { project ->
                    outputPrinter.showMessage("Project Details:")
                    outputPrinter.showMessage("Name: ${project.name}")
                    val stateName: String =
                        manageStatesUseCase.getProjectStateNameByStateId(project.stateId) ?: "not exist state"
                    outputPrinter.showMessage("State: $stateName")
                }
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


        val userId = currentUser?.id

        if (userId == null) {
            outputPrinter.showMessage(UiMessages.USER_NOT_LOGGED_IN)
            return
        }

        CoroutineScope(Dispatchers.IO).launch(errorHandler) {
            manageProjectUseCase.addProject(projectName, stateName, userId).let { success ->
                withContext(Dispatchers.Main) {
                    if (success)
                        outputPrinter.showMessage("Project added successfully")
                    else
                        outputPrinter.showMessage("Failed to add project")
                }
            }
        }
    }

    override fun editProject() {

        outputPrinter.showMessage("Enter project Name to edit or leave it black to back: ")
        val projectName = inputReader.readStringOrNull() ?: return


        CoroutineScope(Dispatchers.IO).launch(errorHandler) {
            getProjectsUseCase.getProjectByName(projectName).let { project ->
                withContext(Dispatchers.Main) {
                    val projectStateName: String =
                        manageStatesUseCase.getProjectStateNameByStateId(project.stateId) ?: "not exist state"
                    outputPrinter.showMessage("Enter new project name (leave blank to keep '${project.name}'): ")

                    val newName = inputReader.readStringOrNull()

                    outputPrinter.showMessage("Current state: $projectStateName")

                    outputPrinter.showMessage("Available states:")

                    stateManagerUi.showAllStates()

                    outputPrinter.showMessage("Enter new state Name (leave blank to keep '${projectStateName}'): ")

                    val newProjectStateName = inputReader.readStringOrNull()

                    val userId = currentUser?.id
                        ?: return@withContext outputPrinter.showMessage(UiMessages.USER_NOT_LOGGED_IN)

                    if (newName != null || newProjectStateName != null)
                        manageProjectUseCase.updateProject(
                            project.id,
                            newName ?: projectName,
                            newProjectStateName ?: projectStateName,
                            userId
                        ).let { success ->
                            if (success) {
                                outputPrinter.showMessage("Project updated successfully")
                            } else {
                                outputPrinter.showMessage("Failed to update project")
                            }

                        }
                    else outputPrinter.showMessage("project doesn't changed")
                }
            }
        }
    }

    override fun deleteProject() {
        outputPrinter.showMessage("Enter project Name to delete or leave it blank to back: ")
        val projectName = inputReader.readStringOrNull() ?: return

        CoroutineScope(Dispatchers.IO).launch(errorHandler) {
            manageProjectUseCase.removeProjectByName(projectName).let { success ->
                withContext(Dispatchers.Main) {
                    if (success) {
                        outputPrinter.showMessage("Project deleted successfully")
                    } else {
                        outputPrinter.showMessage("Failed to delete project")
                    }
                }
            }
        }
    }

    override fun launchUi(user: User?) {
        this.currentUser = user

        if (currentUser == null) {
            outputPrinter.showMessage(UiMessages.INVALID_USER)
            return
        }

        while (true) {
            outputPrinter.showMessage("\nProject Management:")
            outputPrinter.showMessage("1. Show all projects")
            outputPrinter.showMessage("2. Show project details")
            outputPrinter.showMessage("3. Add project")
            outputPrinter.showMessage("4. Edit project State")
            outputPrinter.showMessage("5. Delete project")
            outputPrinter.showMessage("0. Back")
            outputPrinter.showMessage("Enter your choice: ")

            when (inputReader.readStringOrNull()) {
                "1" -> showAllProjects()
                "2" -> showProjectByName()
                "3" -> addProject()
                "4" -> editProject()
                "5" -> deleteProject()
                "0" -> return
                else -> outputPrinter.showMessage("Invalid choice")
            }
        }
    }
}