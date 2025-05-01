package org.example.ui.features.project

import logic.model.entities.Project
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUi

class ProjectManagerUi(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase,
    private val stateManagerUi: AdminStateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val createUserUi: CreateUserUi,
) : UiLauncher {

    fun showAllProjects() {
        manageProjectUseCase.getAllProjects()
            .fold(
                onSuccess = { projects ->
                    if (projects.isEmpty()) {
                        outputPrinter.showMessage("No projects found")
                    } else {
                        projects.forEach { project ->
                            outputPrinter.showMessage("${project.id} -> ${project.name}")
                        }
                    }
                },
                onFailure = { e ->
                    outputPrinter.showMessage("error: ${e.message ?: "Unknown error"}")
                }
            )
    }

    fun showProjectById() {
        outputPrinter.showMessage("Enter project ID: ")
        val id = inputReader.readStringOrNull() ?: run {
            outputPrinter.showMessage("Invalid project ID")
            return
        }

        manageProjectUseCase.getProjectById(id)
            .fold(
                onSuccess = { project ->
                    outputPrinter.showMessage("Project Details:")
                    outputPrinter.showMessage("ID: ${project.id}")
                    outputPrinter.showMessage("Name: ${project.name}")
                    outputPrinter.showMessage("State: ${project.stateId}")
                },
                onFailure = { e ->
                    outputPrinter.showMessage("error: ${e.message ?: "Project not found"}")
                }
            )
    }

    fun addProject() {
        outputPrinter.showMessage("Enter project name: ")
        val name = inputReader.readStringOrNull() ?: run {
            outputPrinter.showMessage("Invalid project name")
            return
        }

        outputPrinter.showMessage("Available states:")
        stateManagerUi.showAllStates()

        var stateId = ""
        while (true) {
            outputPrinter.showMessage("Enter state ID (or 'new' to create a new state): ")
            when (val input = inputReader.readStringOrNull()) {
                "new" -> stateManagerUi.addState()
                null -> continue
                else -> {
                    stateId = input
                    break
                }
            }
        }

        manageProjectUseCase.addProject(Project(name = name, stateId = stateId))
            .fold(
                onSuccess = { success ->
                    if (success) {
                        outputPrinter.showMessage("Project added successfully")
                        outputPrinter.showMessage("Would you like to add tasks to this project? (yes/no): ")
                        if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                            taskManagerUi.addTask()
                        }
                    } else {
                        outputPrinter.showMessage("Failed to add project")
                    }
                },
                onFailure = { e ->
                    outputPrinter.showMessage("error: ${e.message ?: "Failed to add project"}")
                }
            )
    }

    fun editProject() {
        outputPrinter.showMessage("Enter project ID to edit: ")
        val id = inputReader.readStringOrNull() ?: run {
            outputPrinter.showMessage("Invalid project ID")
            return
        }

        manageProjectUseCase.getProjectById(id)
            .fold(
                onSuccess = { project ->
                    outputPrinter.showMessage("Enter new project name (leave blank to keep '${project.name}'): ")
                    val newName = inputReader.readStringOrNull() ?: project.name

                    outputPrinter.showMessage("Current state: ${project.stateId}")
                    outputPrinter.showMessage("Available states:")
                    stateManagerUi.showAllStates()

                    outputPrinter.showMessage("Enter new state ID (leave blank to keep '${project.stateId}'): ")
                    val newStateId = inputReader.readStringOrNull() ?: project.stateId

                    manageProjectUseCase.updateProject(Project(id, newName, newStateId))
                        .fold(
                            onSuccess = { success ->
                                if (success) {
                                    outputPrinter.showMessage("Project updated successfully")
                                } else {
                                    outputPrinter.showMessage("Failed to update project")
                                }
                            },
                            onFailure = { e ->
                                outputPrinter.showMessage("error: ${e.message ?: "Failed to update project"}")
                            }
                        )
                },
                onFailure = { e ->
                    outputPrinter.showMessage("error: ${e.message ?: "Project not found"}")
                }
            )
    }

    fun deleteProject() {
        outputPrinter.showMessage("Enter project ID to delete: ")
        val id = inputReader.readStringOrNull() ?: run {
            outputPrinter.showMessage("Invalid project ID")
            return
        }

        manageProjectUseCase.removeProjectById(id)
            .fold(
                onSuccess = { success ->
                    if (success) {
                        outputPrinter.showMessage("Project deleted successfully")
                    } else {
                        outputPrinter.showMessage("Failed to delete project")
                    }
                },
                onFailure = { e ->
                    outputPrinter.showMessage("error: ${e.message ?: "Failed to delete project"}")
                }
            )
    }

    fun assignUsersToProject() {
        while (true) {
            outputPrinter.showMessage("Would you like to add a new user first? (yes/no): ")
            if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                createUserUi.launchUi()
            }

            outputPrinter.showMessage("Enter username to assign (or 'done' to finish): ")
            val username = inputReader.readStringOrNull() ?: continue
            if (username.equals("done", ignoreCase = true)) break

            outputPrinter.showMessage("Enter project ID: ")
            val projectId = inputReader.readStringOrNull() ?: continue

            assignUserToProject(username, projectId)
        }
    }

    private fun assignUserToProject(username: String, projectId: String): Boolean {
//        if (authenticationUseCase.checkUserExists(username)) {
//            outputPrinter.showMessage("User does not exist")
//            return false
//        }

        if (manageProjectUseCase.isProjectExists(projectId).isFailure) {
            outputPrinter.showMessage("Project does not exist")
            return false
        }

        return manageUsersAssignedToProjectUseCase.assignUserToProject(username, projectId)
            .fold(
                onSuccess = { success ->
                    if (success) {
                        outputPrinter.showMessage("User assigned successfully")
                        true
                    } else {
                        outputPrinter.showMessage("Failed to assign user to project")
                        false
                    }
                },
                onFailure = { e ->
                    outputPrinter.showMessage("error: ${e.message ?: "Failed to assign user to project"}")
                    false
                }
            )
    }

    override fun launchUi() {
        while (true) {
            outputPrinter.showMessage("\nProject Management:")
            outputPrinter.showMessage("1. Show all projects")
            outputPrinter.showMessage("2. Show project details")
            outputPrinter.showMessage("3. Add project")
            outputPrinter.showMessage("4. Edit project")
            outputPrinter.showMessage("5. Delete project")
            outputPrinter.showMessage("6. Assign users to project")
            outputPrinter.showMessage("0. Exit")
            outputPrinter.showMessage("Enter your choice: ")

            when (inputReader.readStringOrNull()) {
                "1" -> showAllProjects()
                "2" -> showProjectById()
                "3" -> addProject()
                "4" -> editProject()
                "5" -> deleteProject()
                "6" -> assignUsersToProject()
                "0" -> return
                else -> outputPrinter.showMessage("Invalid choice")
            }
        }
    }
}