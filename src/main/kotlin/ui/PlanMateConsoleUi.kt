package org.example.ui

import logic.model.entities.User
import logic.model.entities.UserRole
import org.example.ui.features.addusertoProject.AddUserToProjectUI
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.login.LoginUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.StateManageUi
import org.example.ui.features.task.TaskManagerUiImp
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.UiMessages


class PlanMateConsoleUi(
    private val loginUi: LoginUi,
    private val manageAuditSystemUi: AuditSystemManagerUi,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUiImp: StateManageUi,
    private val taskManagerUiImp: TaskManagerUiImp,
    private val addUserToProjectUI: AddUserToProjectUI,
    private val printer: OutputPrinter,
    private val reader: InputReader,
    private val createUserUiImp: CreateUserUi
) {
    fun invoke() {
        while (true) {
            user = manageAuthenticationUI()
            this.handleBasedOnRole()
        }
    }

    private fun handleBasedOnRole() {
        when (user?.userRole) {
            UserRole.ADMIN -> handleAdminUi()
            UserRole.MATE -> handleMateUi()
            null -> return
        }
    }

    private fun handleMateUi() {
        printer.showMessage(UiMessages.MAIN_MENU_WELCOME_MESSAGE_FOR_MATE)
        handleMateChoice()
    }

    private fun logout() {
        printer.showMessage("Thank you for using PlanMate system")
        printer.showMessage("--------------------------")
        user = null
    }

    private fun showErrorChoice() {
        printer.showMessage("Invalid input, please try again")
        printer.showMessage("--------------------------")
    }

    private fun handleMateChoice() {
        reader.readIntOrNull().takeIf { it != null }.let { choice ->
            when (choice) {
                MateChoice.MANAGE_TASKS.choice -> taskManagerUiImp.launchUi(user)
                MateChoice.MANAGE_STATES.choice -> stateManagerUiImp.launchUi(user)
                MateChoice.SHOW_AUDIT_LOG.choice -> manageAuditSystemUi.invoke(user)
                MateChoice.LOGOUT.choice -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun handleAdminUi() {
        printer.showMessage(UiMessages.MAIN_MENU_WELCOME_MESSAGE_FOR_ADMIN)
        reader.readIntOrNull().takeIf { it != null }.let { choice ->
            when (choice) {
                AdminChoice.MANAGE_PROJECTS.choice -> manageProjectUi.launchUi(user)
                AdminChoice.MANAGE_TASKS.choice -> taskManagerUiImp.launchUi(user)
                AdminChoice.MANAGE_STATES.choice -> stateManagerUiImp.launchUi(user)
                AdminChoice.ADD_MATE.choice -> createUserUiImp.launchUi(user)
                AdminChoice.ADD_MATE_TO_PROJECT.choice -> addUserToProjectUI.invoke(user = user)
                AdminChoice.SHOW_AUDIT_LOG.choice -> manageAuditSystemUi.invoke(user)
                AdminChoice.LOGOUT.choice -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun manageAuthenticationUI(): User? {
        if (user != null) return user
        return loginUi.authenticateUser().also {
            user = it
        }
    }

    companion object {
        private var user: User? = null
    }
}

enum class MateChoice(
    val choice: Int
) {
    MANAGE_TASKS(1),
    MANAGE_STATES(2),
    SHOW_AUDIT_LOG(3),
    LOGOUT(4),
}

enum class AdminChoice(
    val choice: Int
) {
    MANAGE_PROJECTS(1),
    MANAGE_TASKS(2),
    MANAGE_STATES(3),
    ADD_MATE(4),
    ADD_MATE_TO_PROJECT(5),
    SHOW_AUDIT_LOG(6),
    LOGOUT(7),
}
