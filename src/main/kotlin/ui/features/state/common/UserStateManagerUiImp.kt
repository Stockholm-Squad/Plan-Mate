package org.example.ui.features.state.common

import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase

open class UserStateManagerUiImp(
    private val manageStatesUseCase: ManageStatesUseCase,
    private val printer: OutputPrinter
) : UserStateManagerUi {
    override fun showAllStates() {
        TODO("Not yet implemented")
    }
}