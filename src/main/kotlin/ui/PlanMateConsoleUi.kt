package org.example.ui

import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.ui.features.addusertoproject.AddUserToProjectUI
import org.example.ui.features.audit.AuditManagerUI
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.login.LoginUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.EntityStateManageUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class PlanMateConsoleUi(
    private val loginUi: LoginUi,
    private val manageAuditUi: AuditManagerUI,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: EntityStateManageUi,
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
        handleMateChoice()
    }

    private fun logout() {
        printer.showMessageLine("Thank you for using PlanMate system")
        printer.showMessageLine("--------------------------")
        loginUi.logout()
    }

    private fun showErrorChoice() {
        printer.showMessageLine("Invalid input, please try again")
        printer.showMessageLine("--------------------------")
    }

    private fun handleMateChoice() {
        reader.readIntOrNull().takeIf { choice -> choice != null }.let { choice ->
            when (choice) {
                MateChoice.MANAGE_TASKS.choice -> taskManagerUi.launchUi()
                MateChoice.MANAGE_STATES.choice -> stateManagerUi.launchUi()
                MateChoice.SHOW_AUDIT_LOG.choice -> manageAuditUi.launchUi()
                MateChoice.LOGOUT.choice -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun handleAdminUi() {
        printer.showMessageLine(UiMessages.MAIN_MENU_WELCOME_MESSAGE_FOR_ADMIN)
        reader.readIntOrNull().takeIf { choice -> choice != null }.let { choice ->
            when (choice) {
                AdminChoice.MANAGE_PROJECTS.choice -> manageProjectUi.launchUi()
                AdminChoice.MANAGE_TASKS.choice -> taskManagerUi.launchUi()
                AdminChoice.MANAGE_STATES.choice -> stateManagerUi.launchUi()
                AdminChoice.ADD_MATE.choice -> createUserUi.launchUi()
                AdminChoice.ADD_MATE_TO_PROJECT.choice -> addUserToProjectUI.launchUi()
                AdminChoice.SHOW_AUDIT_LOG.choice -> manageAuditUi.launchUi()
                AdminChoice.LOGOUT.choice -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun manageAuthenticationUI(): User? {
        return loginUi.authenticateUser()
    }
}

enum class MateChoice(
    val choice: Int,
) {
    MANAGE_TASKS(1),
    MANAGE_STATES(2),
    SHOW_AUDIT_LOG(3),
    LOGOUT(0),
}

enum class AdminChoice(
    val choice: Int,
) {
    MANAGE_PROJECTS(1),
    MANAGE_TASKS(2),
    MANAGE_STATES(3),
    ADD_MATE(4),
    ADD_MATE_TO_PROJECT(5),
    SHOW_AUDIT_LOG(6),
    LOGOUT(0),
}
