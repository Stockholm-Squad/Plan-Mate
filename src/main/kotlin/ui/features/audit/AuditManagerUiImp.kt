package org.example.ui.features.audit

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class AuditManagerUiImp(
    private val useCase: GetAuditUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
    private val loginUseCase: LoginUseCase,
) : AuditManagerUi {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        printer.showMessageLine(throwable.message ?: "Unknown error")
    }

    override fun invoke() {
        if (loginUseCase.getCurrentUser() == null) return
        do {
            printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS)
            when (getMainMenuOption()) {
                1 -> displayAuditsByProjectName()
                2 -> displayAuditsByTaskName()
                3 -> displayAllAudits()
                4 -> printer.showMessageLine(UiMessages.EXITING)
                else -> printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE)
            }
        } while (askSearchAgain() == true)
        printer.showMessageLine(UiMessages.EXITING)
    }


    private fun displayAuditsByProjectName() {
        printer.showMessageLine(UiMessages.PROMPT_PROJECT_NAME)
        reader.readStringOrNull()?.let { input ->
            runBlocking(errorHandler) {
                try{
                    val audits = useCase.getAuditsForProjectByName(input)
                    printer.showAudits(audits, loginUseCase.getCurrentUser()!!.username)
                } catch (e: Exception) {
                    printer.showMessageLine(e.message ?: "Unknown error")
                }
            }
        } ?: printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE)
    }

    private fun displayAuditsByTaskName() {
        printer.showMessageLine(UiMessages.PROMPT_TASK_NAME)
        reader.readStringOrNull()?.let { input ->
            runBlocking {
                try {
                    val audits = useCase.getAuditsForTaskByName(input)
                    printer.showAudits(audits, loginUseCase.getCurrentUser()!!.username)

                }catch (e: Exception) {
                    printer.showMessageLine(e.message ?: "Unknown error")
                }
            }
        } ?: printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE)

    }


    private fun displayAllAudits() {
        runBlocking(errorHandler) {
            try{
                val audits = useCase.getAuditsForUserById(loginUseCase.getCurrentUser()!!.id)
                printer.showAudits(audits, loginUseCase.getCurrentUser()!!.username)
            }catch (e: Exception){
                printer.showMessageLine(e.message ?: "Unknown error")
            }
        }
    }

    private fun askSearchAgain(): Boolean? {
        printer.showMessageLine(UiMessages.SEARCH_AGAIN_PROMPT)
        val input = reader.readStringOrNull()?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        return if (input?.trim()?.lowercase() == UiMessages.Y) true else null
    }

    private fun getMainMenuOption(): Int {
        printer.showMessageLine(UiMessages.PLEASE_SELECT_OPTION)
        return reader.readStringOrNull()?.toIntOrNull() ?: 0
    }

}