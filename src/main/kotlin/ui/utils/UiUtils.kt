package org.example.ui.utils


import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class UiUtils(
    private val printer: OutputPrinter,
) {

    fun readNonBlankInputOrNull(reader: InputReader): String? {
        return reader.readStringOrNull()?.takeIf { it.isNotBlank() }?.lowercase()
    }

    fun invalidChoice() {
        printer.showMessage(UiMessages.INVALID_OPTION)
    }

    fun exit() {
        printer.showMessage(UiMessages.GOODBYE)
    }

}