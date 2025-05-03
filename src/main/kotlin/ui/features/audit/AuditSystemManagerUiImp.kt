package org.example.ui.features.audit

import logic.model.entities.User
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.Constant

class AuditSystemManagerUiImp(
    private val useCase: ManageAuditSystemUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
    private val user: User
) : AuditSystemManagerUi {

    override fun invoke() {
        do {
            printer.showMessage(Constant.SHOW_AUDIT_SYSTEM_OPTIONS)
            when (getMainMenuOption()) {
                1 -> displayAuditsByProjectName()
                2 -> displayAuditsByTaskName()
                3 -> displayAllAudits()
                4 -> printer.showMessage(Constant.EXITING)
                else -> printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
            }
        } while (shouldSearchAgain(reader) == true)
        printer.showMessage(Constant.EXITING)
    }


    private fun displayAuditsByProjectName() {
        printer.showMessage(Constant.PROMPT_PROJECT_NAME)
        reader.readStringOrNull()?.let {
            useCase.getProjectAuditsByName(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
    }

    private fun displayAuditsByTaskName() {
        printer.showMessage(Constant.PROMPT_TASK_NAME)
        reader.readStringOrNull()?.let {
            useCase.getTaskAuditsByName(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
    }


    private fun displayAllAudits() {
        useCase.getAuditsByUserId(user.id).fold(
            onSuccess = { audits -> printer.showAudits(audits) },
            onFailure = { printer.showMessage(it.message.toString()) }
        )
    }

    fun shouldSearchAgain(reader: InputReader): Boolean? {
        printer.showMessage(Constant.SEARCH_AGAIN_PROMPT)
        val input = reader.readStringOrNull()?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        return if (input?.trim()?.lowercase() == Constant.Y) true else null
    }

    fun getMainMenuOption(): Int {
        printer.showMessage(Constant.PLEASE_SELECT_OPTION)
        return reader.readStringOrNull()?.toIntOrNull() ?: 0
    }

}