package org.example.ui.features.audit

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class AuditSystemManagerUiImp(
    private val useCase: GetAuditUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
    private val loginUseCase: LoginUseCase,
) : AuditSystemManagerUi {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        printer.showMessage(throwable.message ?: "Unknown error")
    }

    override fun invoke() {
        if (loginUseCase.getCurrentUser() == null) return
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
                    val audits = useCase.getAuditsForProjectByName(input)
                    printer.showAudits(audits, loginUseCase.getCurrentUser()!!.username)
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
                    val audits = useCase.getAuditsForTaskByName(input)
                    printer.showAudits(audits, loginUseCase.getCurrentUser()!!.username)

                }catch (e: Exception) {
                    printer.showMessage(e.message ?: "Unknown error")
                }
            }
        } ?: printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE)

    }


    private fun displayAllAudits() {
        runBlocking(errorHandler) {
            try{
                val audits = useCase.getAuditsForUserById(loginUseCase.getCurrentUser()!!.id)
                printer.showAudits(audits, loginUseCase.getCurrentUser()!!.username)
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