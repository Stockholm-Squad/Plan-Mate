package org.example.ui.features.login

import logic.model.entities.User
import logic.usecase.login.LoginUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class LoginUi(
    private val getAuthenticationUseCase: LoginUseCase,
    private val printer: OutputPrinter,
    private var reader: InputReader
) {
    fun authenticateUser(): User? {
        printer.showMessage("Please enter your user name: ")
        return reader.readStringOrNull()?.let { username ->
            printer.showMessage("Please enter your Password: ")
            reader.readStringOrNull()?.let { password ->
                getAuthenticationUseCase.loginUser(username = username, password = password)
                    .fold(onSuccess = { user -> user }, onFailure = {
                        handleFailure(it.message.toString())
                        null
                    })
            }
        }
    }

    private fun handleFailure(message: String) {
        printer.showMessage(message)
    }
}