package org.example.ui.features.authentication

import logic.model.entities.User
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.authentication.AuthenticateUseCase


class AuthenticateUi(
    private val getAuthenticationUseCase: AuthenticateUseCase,
    private val printer: OutputPrinter,
    private var reader: InputReader
) {
    fun authenticateUser(): User? {
        printer.showMessage("Enter user name")
        return reader.readStringOrNull()?.let { username ->
            printer.showMessage("Enter Password")
            reader.readStringOrNull()?.let { password ->
                getAuthenticationUseCase.authUser(username = username, password = password)
                    .fold(onSuccess = { user -> user }, onFailure = {
                        handleFailure(it.message.toString())
                        null
                    })
            }
        }.also {
            printer.showMessage("Invalid input")
        }.also {
            printer.showMessage("Invalid input")
        }
    }

    private fun handleFailure(message: String) {
        printer.showMessage(message)
    }
}