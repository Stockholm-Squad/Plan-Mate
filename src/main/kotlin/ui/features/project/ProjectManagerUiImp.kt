package org.example.ui.features.project

import logic.model.entities.User
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class ProjectManagerUiImp(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase,
    private val stateManagerUi: AdminStateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val manageStatesUseCase: ManageStatesUseCase,
    private val createUserUiImp: CreateUserUi,
) : ProjectManagerUi {

    override fun showAllProjects() {
        manageProjectUseCase.getAllProjects().fold(
            onSuccess = { projects ->
                if (projects.isEmpty()) {
                    outputPrinter.showMessage("No projects found")
                } else {
                    projects.forEachIndexed { index, project ->
                        outputPrinter.showMessage("${index + 1}. ${project.name}")
                    }
                }
            },
            onFailure = { e ->
                outputPrinter.showMessage("error: ${e.message ?: "Unknown error"}")
            }
        )
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

        manageProjectUseCase.getProjectByName(projectName)
            .fold(
                onSuccess = { project ->
                    outputPrinter.showMessage("Project Details:")
                    outputPrinter.showMessage("Name: ${project.name}")
                     val stateName :String = manageStatesUseCase.getProjectStateNameByStateId(project.stateId) ?: "not exist state"
                    outputPrinter.showMessage("State: $stateName")
                },
                onFailure = { e ->
                    outputPrinter.showMessage(e.message ?: "Project not found")
                }
            )
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

        manageProjectUseCase.addProject(projectName, stateName)
            .fold(
                onSuccess = { success ->
                    extractedAddingProjectResult(success)
                },
                onFailure = { e ->
                    outputPrinter.showMessage(e.message ?: "Failed to add project")
                }
            )
    }

    private fun extractedAddingProjectResult(success: Boolean) {
        if (success) {
            outputPrinter.showMessage("Project added successfully")
            outputPrinter.showMessage("Would you like to add tasks to this project? (yes/no): ")
            if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                taskManagerUi.createTask()
            }
        } else {
            outputPrinter.showMessage("Failed to add project")
        }
    }

    override fun editProject() {

        outputPrinter.showMessage("Enter project Name to edit or leave it black to back: ")
        val projectName = inputReader.readStringOrNull() ?: run {
            return
        }

        manageProjectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                val projectStateName :String = manageStatesUseCase.getProjectStateNameByStateId(project.stateId) ?: "not exist state"
                outputPrinter.showMessage("Enter new project name (leave blank to keep '${project.name}'): ")

                val newName = inputReader.readStringOrNull()

                outputPrinter.showMessage("Current state: $projectStateName")

                outputPrinter.showMessage("Available states:")

                stateManagerUi.showAllStates()

                outputPrinter.showMessage("Enter new state Name (leave blank to keep '${projectStateName}'): ")

                val newProjectStateName = inputReader.readStringOrNull()

                if (newName != null || newProjectStateName != null)
                    manageProjectUseCase.updateProject(
                        project.id,
                        newName ?: projectName,
                        newProjectStateName ?: projectStateName
                    )
                        .fold(
                            onSuccess = { success ->
                                if (success) {
                                    outputPrinter.showMessage("Project updated successfully")
                                } else {
                                    outputPrinter.showMessage("Failed to update project")
                                }
                            },
                            onFailure = { e ->
                                outputPrinter.showMessage(e.message ?: "Failed to update project")
                            }
                        )
                else outputPrinter.showMessage("project doesn't changed")

            },
            onFailure = { e ->
                outputPrinter.showMessage(e.message ?: "Project not found")
            }

        )
    }

    override fun deleteProject() {
        outputPrinter.showMessage("Enter project Name to delete or leave it blank to back: ")
        val projectName = inputReader.readStringOrNull() ?: run {
            return
        }

        manageProjectUseCase.removeProjectByName(projectName)
            .fold(
                onSuccess = { success ->
                    if (success) {
                        outputPrinter.showMessage("Project deleted successfully")
                    } else {
                        outputPrinter.showMessage("Failed to delete project")
                    }
                },
                onFailure = { e ->
                    outputPrinter.showMessage(e.message ?: "Failed to delete project")
                }
            )
    }


    override fun assignUsersToProject(user: User?) {
        while (true) {
            outputPrinter.showMessage("Would you like to add a new user first? (yes/no) leave it blank to back: ")
            if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                createUserUiImp.launchUi(user)
            }

            outputPrinter.showMessage("Enter username to assign or leave it blank to back: ")
            val username = inputReader.readStringOrNull() ?: return
            if (inputReader.readStringOrNull().equals("done", ignoreCase = true)) break

            outputPrinter.showMessage("Enter project Name: ")
            val projectName = inputReader.readStringOrNull() ?: continue

            assignUserToProject(username, projectName)
        }
    }

    private fun assignUserToProject(username: String, projectName: String) {
//        if (authenticationUseCase.checkUserExists(username)) {
//            outputPrinter.showMessage("User does not exist")  //TODO: to be added
//            return
//        }

        manageProjectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                manageUsersAssignedToProjectUseCase.addUserToProject(project.id, username).fold(
                    onSuccess = { success ->
                        if (success) {
                            outputPrinter.showMessage("User assigned successfully")
                        } else {
                            outputPrinter.showMessage("Failed to assign user to project")
                        }
                    },
                    onFailure = { e ->
                        outputPrinter.showMessage(e.message ?: "Failed to assign user to project")
                    }
                )
            },
            onFailure = {
                outputPrinter.showMessage("Project does not exist")
            }
        )
    }


    override fun launchUi(user: User?) {
        while (true) {
            outputPrinter.showMessage("\nProject Management:")
            outputPrinter.showMessage("1. Show all projects")
            outputPrinter.showMessage("2. Show project details")
            outputPrinter.showMessage("3. Add project")
            outputPrinter.showMessage("4. Edit project State")
            outputPrinter.showMessage("5. Delete project")
            outputPrinter.showMessage("6. Assign users to project")
            outputPrinter.showMessage("0. Back")
            outputPrinter.showMessage("Enter your choice: ")

            when (inputReader.readStringOrNull()) {
                "1" -> showAllProjects()
                "2" -> showProjectByName()
                "3" -> addProject()
                "4" -> editProject()
                "5" -> deleteProject()
                "6" -> assignUsersToProject(user)
                "0" -> return
                else -> outputPrinter.showMessage("Invalid choice")
            }
        }
    }
}