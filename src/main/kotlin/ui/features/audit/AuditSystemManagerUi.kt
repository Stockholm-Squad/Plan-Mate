package org.example.ui.features.audit

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.utils.Constant
import org.example.utils.SearchUtils

class AuditSystemManagerUi(
    private val useCase: ManageAuditSystemUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
    private val searchUtils: SearchUtils
) : AuditSystemManagerUII {

    override fun showAuditSystemManagerUI() {
        do {
            when (searchUtils.getMainMenuOption()) {
                1 -> displayAllAuditSystems()
                2 -> displayAuditLogsByTaskId()
                3 -> displayAuditLogsByProjectId()
                4 -> displayAuditByAuditId()
                5 -> displayAuditByUsername()
                6 -> printer.showMessage(Constant.EXITING)
                else -> printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
            }
        } while (searchUtils.shouldSearchAgain(reader) == true)
        printer.showMessage(Constant.EXITING)
    }

    private fun displayAllAuditSystems() =
        useCase.getAllAuditSystems().fold(
            onSuccess = { audits -> printer.showAudits(audits) },
            onFailure = { printer.showMessage(it.message.toString()) }
        )

    private fun displayAuditLogsByProjectId() {
        printer.showMessage(Constant.PROMPT_PROJECT_ID)
        reader.readStringOrNull()?.let {
            useCase.getProjectChangeLogsById(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)

    }

    private fun displayAuditLogsByTaskId() {
        printer.showMessage(Constant.PROMPT_TASK_ID)
        reader.readStringOrNull()?.let {
            useCase.getTaskChangeLogsById(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
    }

    private fun displayAuditByAuditId() {
        printer.showMessage(Constant.PROMPT_AUDIT_SYSTEM_ID)
        reader.readStringOrNull()?.let {
            useCase.getAuditSystemByID(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
    }

    private fun displayAuditByUsername() {
        printer.showMessage(Constant.PROMPT_USERNAMES)
        reader.readStringOrNull()?.let {
            useCase.getUserChangeLogsByUsername(it).fold(
                onSuccess = { audits -> printer.showAudits(audits) },
                onFailure = { printer.showMessage(it.message.toString()) }
            )
        } ?: printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
    }


}