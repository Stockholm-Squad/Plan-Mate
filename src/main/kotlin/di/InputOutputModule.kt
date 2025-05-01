package org.example.di

import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.input.InputReaderImplementation
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.input_output.output.OutputPrinterImplementation
import org.koin.dsl.module

val inputOutputModule = module {
    factory<OutputPrinter> { OutputPrinterImplementation() }
    factory<InputReader> { InputReaderImplementation() }
}
