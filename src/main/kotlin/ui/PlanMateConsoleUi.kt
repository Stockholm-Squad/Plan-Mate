package org.example.ui

import logic.model.entities.Role
import logic.model.entities.User
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.StateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.utils.Constant


class PlanMateConsoleUi(
    private val manageAuthenticationUi: AuthenticationManagerUi,
    private val manageAuditSystemUi: AuditSystemManagerUi,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: StateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val printer: OutputPrinter,
    private val reader: InputReader
) {
    fun invoke() {
        while (true) {
            this.manageAuthenticationUI()
            this.handleBasedOnRole()
        }
    }

    private fun handleBasedOnRole() {
        when (user?.role) {
            Role.ADMIN -> handleAdminUi()
            Role.MATE -> handleMateUi()
            null -> printer.showMessage("User not found, please try again")
        }
    }

    private fun handleMateUi() {
        printer.showMessage(Constant.MAIN_MENU_WELCOME_MESSAGE_FOR_MATE)
        handleMateChoice()
    }

    private fun logout() {
        printer.showMessage("Thank you for using PlanMate system")
        user = null
    }

    private fun showErrorChoice() {
        printer.showMessage("Invalid input, please try again")
    }

    private fun handleMateChoice() {
        reader.readIntOrNull().takeIf { it != null }.let { choice ->
            when (choice) {
                MateChoice.MANAGE_TASKS.choice -> TODO()
                MateChoice.MANAGE_STATES.choice -> stateManagerUi.launchStateManagerUi(user?.role)
                MateChoice.SHOW_AUDIT_LOG.choice -> manageAuditSystemUi.showAuditSystemManagerUI()
                MateChoice.LOGOUT.choice -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun handleAdminUi() {
        printer.showMessage(Constant.MAIN_MENU_WELCOME_MESSAGE_FOR_ADMIN)
        reader.readIntOrNull().takeIf { it != null }.let { choice ->
            when (choice) {
                AdminChoice.MANAGE_PROJECTS.choice -> TODO()
                AdminChoice.MANAGE_TASKS.choice -> TODO()
                AdminChoice.MANAGE_STATES.choice -> stateManagerUi.launchStateManagerUi(user?.role)
                AdminChoice.ADD_MATE.choice -> TODO()
                AdminChoice.SHOW_AUDIT_LOG.choice -> manageAuditSystemUi.showAuditSystemManagerUI()
                AdminChoice.LOGOUT.choice -> logout()
                else -> showErrorChoice()
            }
        }
    }

    private fun manageAuthenticationUI(): User? {
        if (user != null) return user
        return User("", "")//manageAuthenticationUi.launch()
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
    SHOW_AUDIT_LOG(5),
    LOGOUT(6),
}