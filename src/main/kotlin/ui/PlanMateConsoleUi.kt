package org.example.ui

import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.ui.features.addusertoproject.AddUserToProjectUI
import org.example.ui.features.audit.AuditManagerUI
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.login.LoginUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.EntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class PlanMateConsoleUi(
    private val loginUi: LoginUi,
    private val manageAuditUi: AuditManagerUI,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: EntityStateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val addUserToProjectUI: AddUserToProjectUI,
    private val createUserUi: CreateUserUi,
    private val printer: OutputPrinter,
    private val reader: InputReader,
) {
    fun invoke() {
        while (true) {
            val user = manageAuthenticationUI()
            this.handleBasedOnRole(user)
        }
    }

    private fun handleBasedOnRole(user: User?) {
        when (user?.userRole) {
            UserRole.ADMIN -> handleAdminUi()
            UserRole.MATE -> handleMateUi()
            null -> return
        }
    }

    private fun handleMateUi() {
        printer.showMessageLine(UiMessages.MAIN_MENU_WELCOME_MESSAGE_FOR_MATE)
        printer.showMessage(UiMessages.SELECT_OPTION)
        handleMateChoice()
    }

    private fun logout() {
        printer.showMessageLine(UiMessages.GOODBYE)
        printer.showMessageLine(UiMessages.LINE_SEPARATOR)
        loginUi.logout()
    }

    private fun showErrorChoice() {
        printer.showMessageLine("${UiMessages.INVALID_INPUT} ${UiMessages.PLEASE_TRY_AGAIN}")
        printer.showMessageLine(UiMessages.LINE_SEPARATOR)
    }

    private fun handleMateChoice() {
        reader.readIntOrNull().takeIf { choice -> choice != null }.let { choice ->
            when (choice) {
                1 -> taskManagerUi.launchUi()
                2 -> stateManagerUi.launchUi()
                3 -> manageAuditUi.launchUi()
                0 -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun handleAdminUi() {
        printer.showMessageLine(UiMessages.MAIN_MENU_WELCOME_MESSAGE_FOR_ADMIN)
        printer.showMessage(UiMessages.SELECT_OPTION)
        reader.readIntOrNull().takeIf { choice -> choice != null }.let { choice ->
            when (choice) {
                1 -> manageProjectUi.launchUi()
                2 -> taskManagerUi.launchUi()
                3 -> stateManagerUi.launchUi()
                4 -> createUserUi.launchUi()
                5 -> addUserToProjectUI.launchUi()
                6 -> manageAuditUi.launchUi()
                0 -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun manageAuthenticationUI(): User? {
        return loginUi.authenticateUser()
    }
}

