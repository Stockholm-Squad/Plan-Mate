package org.example.utils

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter

class SearchUtils(
    private val printer: OutputPrinter,
) {

    /**
     * Validates if the input matches "y" (for search again).
     */
    private fun isValidSearchAgainInput(input: String?): Boolean =
        input?.trim()?.lowercase() == Constant.Y

    /**
     * Reads and returns non-blank trimmed lowercase input from the user.
     */
    private fun readTrimmedLowercaseInput(reader: InputReader): String? =
        reader.readStringOrNull()?.trim()?.lowercase()?.takeIf { it.isNotBlank() }

    /**
     * Reads and returns non-blank trimmed lowercase input from the user (for general purposes).
     */
    fun readNonBlankTrimmedInput(reader: InputReader): String? =
        reader.readStringOrNull()?.trim()?.lowercase()?.takeIf { it.isNotBlank() }

    /**
     * Checks if the input indicates the user wants to skip (e.g., "n").
     */
    private fun isSkipInput(input: String): Boolean =
        input.trim().lowercase() == Constant.N



    /**
     * Handles invalid input by printing an error message.
     */
    private fun handleInvalidInput() {
        printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
    }


    /**
     * Asks the user whether to search again. Returns true only if user enters "y".
     */
    fun shouldSearchAgain(reader: InputReader): Boolean? {
        printer.showMessage(Constant.SEARCH_AGAIN_PROMPT)
        val input = readTrimmedLowercaseInput(reader)
        return if (isValidSearchAgainInput(input)) true else null
    }
}