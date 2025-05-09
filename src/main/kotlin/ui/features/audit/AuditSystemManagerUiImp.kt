package org.example.ui.features.audit

import kotlinx.coroutines.*
import org.example.logic.entities.User
import org.example.logic.usecase.audit.GetAuditSystemUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class AuditSystemManagerUiImp(
    private val useCase: GetAuditSystemUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
) : AuditSystemManagerUi {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        printer.showMessage(throwable.message ?: "Unknown error")
    }
    private var user: User? = null

    override fun invoke(user: User?) {
        this.user = user
        if (user == null) return
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
        reader.readStringOrNull()?.let { input ->
            runBlocking(errorHandler) {
                try{
                    val audits = useCase.getProjectAuditsByName(input)
                    printer.showAudits(audits, user!!.username)
                } catch (e: Exception) {
                    printer.showMessage(e.message ?: "Unknown error")
                }
            }
        } ?: printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE)
    }

    private fun displayAuditsByTaskName() {
        printer.showMessage(UiMessages.PROMPT_TASK_NAME)
        reader.readStringOrNull()?.let { input ->
            runBlocking {
                try {
                    val audits = useCase.getTaskAuditsByName(input)
                    printer.showAudits(audits, user!!.username)

                }catch (e: Exception) {
                    printer.showMessage(e.message ?: "Unknown error")
                }
            }
        } ?: printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE)

    }


    private fun displayAllAudits() {
        runBlocking(errorHandler) {
            try{
                val audits = useCase.getAuditsByUserId(user!!.id)
                printer.showAudits(audits, user!!.username)
            }catch (e: Exception){
                printer.showMessage(e.message ?: "Unknown error")
            }
        }
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