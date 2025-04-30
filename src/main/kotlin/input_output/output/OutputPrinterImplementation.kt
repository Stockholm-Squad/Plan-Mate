package org.example.input_output.output

import logic.model.entities.State


class OutputPrinterImplementation : OutputPrinter {
    override fun showMessage(message: String) {
        println(message)

    }

    override fun showStates(states: List<State>) {
        this.printStateUsingSwimlaneUi(states)
    }

    private fun printStateUsingSwimlaneUi(states: List<State>) {
        println("┌──────────────────────────────┐")
        println("│ State                        │")
        println("├──────────────────────────────┤")
        states.forEach {
            println("│ %-28s │".format(it.name))
        }
        println("└──────────────────────────────┘")
    }
}