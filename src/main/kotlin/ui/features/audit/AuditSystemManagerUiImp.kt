package org.example.ui.features.audit

import logic.model.entities.User
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.UiMessages

class AuditSystemManagerUiImp(
    private val useCase: ManageAuditSystemUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
) : AuditSystemManagerUi {

    private var user: User? = null

    override fun invoke(user: User?) {
        this.user = user
        do {
            printer.showMessage(UiMessages.SHOW_AUDIT_SYSTEM_OPTIONS)
            when (getMainMenuOption()) {
                1 -> displayAuditsByProjectName()
                2 -> displayAuditsByTaskName()
                3 -> displayAllAudits()
                4 -> printer.showMessage(UiMessages.EXITING)
                else -> printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE)
            }
        } while (askSearchAgain() == true)
        printer.showMessage(UiMessages.EXITING)
    }


    private fun displayAuditsByProjectName() {
        printer.showMessage(UiMessages.PROMPT_PROJECT_NAME)
        reader.readStringOrNull()?.let {
            useCase.getProjectAuditsByName(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE)
    }

    private fun displayAuditsByTaskName() {
        printer.showMessage(UiMessages.PROMPT_TASK_NAME)
        reader.readStringOrNull()?.let {
            useCase.getTaskAuditsByName(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE)

    }


    private fun displayAllAudits() {

        user?.id ?: return
        useCase.getAuditsByUserId(user?.id!!).fold(
            onSuccess = { audits -> printer.showAudits(audits) },
            onFailure = { printer.showMessage(it.message.toString()) }
        )
    }

    private fun askSearchAgain(): Boolean? {
        printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT)
        val input = reader.readStringOrNull()?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        return if (input?.trim()?.lowercase() == UiMessages.Y) true else null
    }

    private fun getMainMenuOption(): Int {
        printer.showMessage(UiMessages.PLEASE_SELECT_OPTION)
        return reader.readStringOrNull()?.toIntOrNull() ?: 0
    }

}