package org.example.ui.features.addusertoProject

import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUiImp
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class AddUserToProjectUIImp(
    private val manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val createUserUiImp: CreateUserUiImp,
) : AddUserToProjectUI {


    override fun addUserToProject() {
        while (true) {
            outputPrinter.showMessage("Would you like to add a new user first? (yes/no): ")
            if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                createUserUiImp.launchUi()
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

        return manageUsersAssignedToProjectUseCase.addUserToProject(username, projectId)
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
}