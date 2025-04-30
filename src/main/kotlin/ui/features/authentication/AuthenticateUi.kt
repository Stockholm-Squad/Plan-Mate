package org.example.ui.features.authentication

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.authentication.AuthenticateUseCase

class AuthenticateUi(private val getAuthenticationUseCase: AuthenticateUseCase, private val printer: OutputPrinter,  private  var reader: InputReader) {
    fun authenticateUser(){

    }
}