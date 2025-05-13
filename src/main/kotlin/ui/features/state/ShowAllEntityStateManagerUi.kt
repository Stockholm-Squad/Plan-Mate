package org.example.ui.features.state

import kotlinx.coroutines.runBlocking
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.output.OutputPrinter

class ShowAllEntityStateManagerUi(
    private val manageEntityStatesUseCase: ManageEntityStatesUseCase,
    private val printer: OutputPrinter,
) : UiLauncher {

    override fun launchUi() {
        runBlocking {
            try {
                manageEntityStatesUseCase.getAllEntityStates().let { states ->
                    printer.showStates(states)
                }
            } catch (exception: Exception) {
                printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_STATE}, ${exception.message}")
            }
        }
    }
}