package org.example.ui.features.addusertoproject

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import org.example.logic.entities.User
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
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
        outputPrinter.showMessageLine(throwable.message ?: UiMessages.UNKNOWN_ERROR)
    }

    override fun launchUi() {
        while (true) {
            outputPrinter.showMessageLine(UiMessages.SHOW_ADD_USER_TO_PROJECT_OPTIONS)
            if (handleUserChoice()) return
        }
    }

    private fun handleUserChoice(): Boolean {
        outputPrinter.showMessage(UiMessages.SELECT_OPTION)
        when (inputReader.readIntOrNull()) {
            1 -> assignUsersToProject()
            2 -> showUsersAssignedToProject()
            3 -> removeUserFromProject()
            0 -> return true
            else -> outputPrinter.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE)
        }
        return false
    }

    private fun assignUsersToProject() {
        do {
            outputPrinter.showMessage(UiMessages.ADD_NEW_USER_FIRST)
            if (inputReader.readStringOrNull().equals(UiMessages.Y, ignoreCase = true)) {
                createUserUiImp.launchUi()
            }

            outputPrinter.showMessage(UiMessages.ENTER_USER_NAME_TO_ASSIGN_TO_PROJECT)
            val username = inputReader.readStringOrNull() ?: return
            if (username.equals("done", ignoreCase = true)) break

            outputPrinter.showMessage(UiMessages.PROJECT_NAME_PROMPT)
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
                    )
                )
            } catch (e: Exception) {
                failedToAssignUserToProject(e)
                false
            }
        }

    private fun failedToAssignUserToProject(e: Exception) {
        outputPrinter.showMessageLine("${UiMessages.FAILED_TO_ASSIGN_USER_TO_PROJECT}, ${e.message}")
        outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN)
    }

    private fun handleAssignUserToProjectMessage(isProjectAdded: Boolean) =
        if (isProjectAdded) {
            outputPrinter.showMessageLine(UiMessages.USER_ASSIGNED_TO_PROJECT)
            true
        } else {
            failedToAssignUserToProject(Exception(""))
            false
        }

    private fun showUsersAssignedToProject() {
        outputPrinter.showMessage(UiMessages.ENTER_PROJECT_NAME_TO_VIEW_ASSIGNED_USER)
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
                    outputPrinter.showMessageLine("${UiMessages.FAILED_LOADING_USER_ASSIGNED_TO_PROJECT}, ${e.message}")
                    outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN)
                }
            }
        }
    }

    private fun handleUsersAssignedToProjectMessages(users: List<User>, input: String) {
        if (users.isEmpty()) {
            outputPrinter.showMessageLine(UiMessages.NO_USERS_ASSIGNED_TO_PROJECT)
        } else {
            outputPrinter.showMessageLine("${UiMessages.USERS_ASSIGNED_TO} $input: ")
            users.forEachIndexed { index, user ->
                outputPrinter.showMessageLine("${index + 1}. ${user.username}")
            }
        }
    }

    private fun removeUserFromProject() {
        outputPrinter.showMessage("${UiMessages.PROMPT_PROJECT_NAME} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
        runBlocking(errorHandler) {
            inputReader.readStringOrNull()?.let { projectName ->
                outputPrinter.showMessage("${UiMessages.ENTER_USER_NAME_TO_REMOVE_PROJECT} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
                inputReader.readStringOrNull()?.let { username ->
                    try {
                        manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, username)
                        outputPrinter.showMessageLine(UiMessages.USER_DELETED_FROM_PROJECT)
                    } catch (e: Exception) {
                        outputPrinter.showMessageLine("${UiMessages.FAILED_TO_DELETE_USER_FROM_PROJECT} ${e.message}")
                        outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN)
                    }
                }
            }
        }
    }
}