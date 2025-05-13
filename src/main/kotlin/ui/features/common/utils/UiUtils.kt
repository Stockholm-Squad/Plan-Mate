package org.example.ui.features.common.utils

import org.example.ui.input_output.input.InputReader

class UiUtils() {

    fun readNonBlankInputOrNull(reader: InputReader): String? {
        return reader.readStringOrNull()?.takeIf { it.isNotBlank() }?.lowercase()
    }
}