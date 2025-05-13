package org.example.ui.features.addusertoproject

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import org.example.logic.entities.User
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class AddUserToProjectUI(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase,
    private val createUserUiImp: CreateUserUi,
) : UiLauncher {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        outputPrinter.showMessageLine(throwable.message ?: "Unknown error")
    }

    override fun launchUi() {
        while (true) {
            showMenu()
            if (handleUserChoice()) return
        }
    }

    private fun handleUserChoice(): Boolean {
        outputPrinter.showMessage("Enter your choice: ")
        when (inputReader.readStringOrNull()) {
            "1" -> assignUsersToProject()
            "2" -> showUsersAssignedToProject()
            "3" -> removeUserFromProject()
            "0" -> return true
            else -> outputPrinter.showMessageLine("Invalid choice, please try again ^_^")
        }
        return false
    }

    private fun showMenu() {
        outputPrinter.showMessageLine("--------------------------------------------------")
        outputPrinter.showMessageLine("Users In Project Management:")
        outputPrinter.showMessageLine("1. Assign users to project")
        outputPrinter.showMessageLine("2. View users assigned to project")
        outputPrinter.showMessageLine("3. Remove user from project")
        outputPrinter.showMessageLine("0. Back")
        outputPrinter.showMessageLine("--------------------------------------------------")
    }

    private fun assignUsersToProject() {
        do {
            outputPrinter.showMessageLine("--------------------------------------------------")
            outputPrinter.showMessage("Would you like to add a new user first? (yes/no): ")
            if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                createUserUiImp.launchUi()
            }

            outputPrinter.showMessage("Enter username to assign or leave it blank to back: ")
            val username = inputReader.readStringOrNull() ?: return
            if (username.equals("done", ignoreCase = true)) break

            outputPrinter.showMessage("Enter project Name: ")
            val projectName = inputReader.readStringOrNull() ?: continue
            if (assignUserToProject(username, projectName)) return

        } while (true)
    }

    private fun assignUserToProject(username: String, projectName: String): Boolean =
        runBlocking(errorHandler) {
            try {
                handleAssignUserToProjectMessage(
                    manageUsersAssignedToProjectUseCase.addUserToProject(
                        getProjectsUseCase.getProjectByName(projectName).id, username
                    ),
                    username,
                    projectName
                )
            } catch (e: Exception) {
                outputPrinter.showMessageLine("Failed to Assign user to project: ${e.message}")
                outputPrinter.showMessageLine("Please try again ^_^")
                false
            }
        }

    private fun handleAssignUserToProjectMessage(isProjectAdded: Boolean, username: String, projectName: String) =
        if (isProjectAdded) {
            outputPrinter.showMessageLine("$username assigned to $projectName successfully ^_^")
            true
        } else {
            outputPrinter.showMessageLine("Failed to assign user to project")
            false
        }

    private fun showUsersAssignedToProject() {
        outputPrinter.showMessage("Enter project name to view assigned users (leave blank to cancel): ")
        inputReader.readStringOrNull()?.let { projectName ->
            runBlocking(errorHandler) {
                try {
                    handleUsersAssignedToProjectMessages(
                        manageUsersAssignedToProjectUseCase.getUsersByProjectId(
                            getProjectsUseCase.getProjectByName(projectName).id
                        ),
                        projectName
                    )
                } catch (e: Exception) {
                    outputPrinter.showMessageLine("Filed on loading Users assigned to $projectName ${e.message}")
                    outputPrinter.showMessageLine("Please try again ^_^")
                }
            }
        }
    }

    private fun handleUsersAssignedToProjectMessages(users: List<User>, input: String) {
        if (users.isEmpty()) {
            outputPrinter.showMessageLine("No users assigned to this project")
        } else {
            outputPrinter.showMessageLine("Users assigned to project '$input':")
            users.forEachIndexed { index, user ->
                outputPrinter.showMessageLine("${index + 1}. ${user.username}")
            }
        }
    }

    private fun removeUserFromProject() {
        outputPrinter.showMessage("Enter project name (leave blank to cancel): ")
        runBlocking(errorHandler) {
            inputReader.readStringOrNull()?.let { projectName ->
                outputPrinter.showMessage("Enter username to remove from project (leave blank to cancel): ")
                inputReader.readStringOrNull()?.let { username ->
                    try {
                        manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, username)
                        outputPrinter.showMessageLine("$username deleted from $projectName Successfully ^_^")
                    } catch (e: Exception) {
                        outputPrinter.showMessageLine("Filed to delete $username from $projectName ${e.message}")
                        outputPrinter.showMessageLine("Please try again ^_^")
                    }
                }
            }
        }
    }
}