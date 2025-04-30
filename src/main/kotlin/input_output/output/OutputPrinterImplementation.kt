package org.example.input_output.output

import logic.model.entities.State


class OutputPrinterImplementation : OutputPrinter {
    override fun showMessage(message: String) {
        println(message)

    }

    override fun showState(state: State) {
        println(state)
    }
}