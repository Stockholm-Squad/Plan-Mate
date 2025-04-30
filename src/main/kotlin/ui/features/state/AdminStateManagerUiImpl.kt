package org.example.ui.features.state

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUi

class AdminStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
    private val manageStatesUseCase: ManageStatesUseCase,
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter
) : AdminStateManagerUi, UserStateManagerUi {

    override fun launchUi() {
        println()
    }

    override fun addState() {
        println()
    }

    override fun editState() {
        println()
    }

    override fun deleteState() {
        println()
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}