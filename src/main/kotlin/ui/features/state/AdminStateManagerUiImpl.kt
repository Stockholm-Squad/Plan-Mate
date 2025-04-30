package org.example.ui.features.state

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUiImp

class AdminStateManagerUiImpl(
    private val manageStatesUseCase: ManageStatesUseCase,
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter
) : AdminStateManagerUi, UserStateManagerUiImp(manageStatesUseCase, outputPrinter) {

    override fun launchUi() {
        TODO("Not yet implemented")
    }

    override fun addState() {
        TODO("Not yet implemented")
    }

    override fun editState() {
        TODO("Not yet implemented")
    }

    override fun deleteState() {
        TODO("Not yet implemented")
    }

    override fun showAllStates() {
        TODO("Not yet implemented")
    }
}