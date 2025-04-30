package org.example.ui.features.state.mate

import org.example.input_output.output.OutputPrinter
import org.example.ui.features.state.common.UserStateManagerUi

class MateStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
    outputPrinter: OutputPrinter
) : MateStateManagerUi, UserStateManagerUi {

    override fun launchUi() {
        this.showAllStates()
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}