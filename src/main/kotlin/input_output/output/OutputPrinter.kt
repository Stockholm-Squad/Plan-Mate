package org.example.input_output.output

import logic.model.entities.State


interface OutputPrinter {
    fun showMessage(message: String)
    fun showState(state: State)
}