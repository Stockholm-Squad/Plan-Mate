package org.example.ui.features.project

import logic.model.entities.Project
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.authentication.ManageAuthenticationUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.state.AdminStateManagerUi
import org.example.ui.features.task.TaskManagerUi

class ProjectManagerUi(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val stateManagerUi: AdminStateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val authenticationManagerUi: AuthenticationManagerUi,
    private val authenticationUseCase: ManageAuthenticationUseCase
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

    fun showProjectById(id: String) {
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


        var stateInput = ""

        while (true) {

            while (true) {
                outputPrinter.showMessage("Enter state ID (or 'new' to create a new state): ")
                val input = inputReader.readStringOrNull() ?: continue
                stateInput = input
                break
            }

            if (stateInput == "new") {
                stateManagerUi.addState()
                continue
            }
            break
        }


        manageProjectUseCase.addProject(Project(name = name, stateId = stateInput))
            .fold(
                onSuccess = { success ->
                    if (success) {
                        outputPrinter.showMessage("Project added successfully")

                        outputPrinter.showMessage("Would you like to add tasks to this project? (yes/no): ")
                        val addTasks = inputReader.readStringOrNull()
                        if (addTasks.equals("yes", ignoreCase = true)) {
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

    fun editProject(id: String) {
        manageProjectUseCase.getProjectById(id)
            .fold(
                onSuccess = { project ->
                    outputPrinter.showMessage("Enter new project name (leave blank to keep current): ")
                    val newName = inputReader.readStringOrNull() ?: project.name

                    outputPrinter.showMessage("Current state: ${project.stateId}")
                    outputPrinter.showMessage("Available states:")
                    stateManagerUi.showAllStates()

                    outputPrinter.showMessage("Enter new state ID (leave blank to keep current): ")
                    val newStateIdInput = inputReader.readStringOrNull()
                    val newStateId = newStateIdInput ?: project.stateId

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

    fun deleteProject(id: String) {
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
            val addUser = inputReader.readStringOrNull()
            if (addUser.equals("yes", ignoreCase = true)) {
                authenticationManagerUi.addUser()
            }

            outputPrinter.showMessage("Enter username to assign (or 'done' to finish): ")
            val username = inputReader.readStringOrNull()
            if (username.equals("done", ignoreCase = true)) break

            outputPrinter.showMessage("Enter project ID: ")
            val projectId = inputReader.readStringOrNull() ?: ""

            assignUserToProject(username ?: "", projectId)
        }
    }

    fun assignUserToProject(username: String, projectId: String): Boolean {
        if (authenticationUseCase.isUserExists(username).isFailure) {
            outputPrinter.showMessage("User does not exist")
            return false
        }

        if (manageProjectUseCase.isProjectExists(projectId).isFailure) {
            outputPrinter.showMessage("Project does not exist")
            return false
        }

        return manageProjectUseCase.assignUsersToProject(username, projectId)
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
        TODO("Not yet implemented")
    }
}