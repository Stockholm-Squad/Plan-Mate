package org.example.ui.features.login

import logic.models.entities.User
import logic.usecase.login.LoginUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class LoginUiImp(
    private val getAuthenticationUseCase: LoginUseCase,
    private val printer: OutputPrinter,
    private var reader: InputReader
) : LoginUi {

    override suspend fun authenticateUser(): User? {
        printer.showMessage("Please enter your user name: ")
        val username = reader.readStringOrNull() ?: return null

        printer.showMessage("Please enter your Password: ")
        val password = reader.readStringOrNull() ?: return null

        return try {
            getAuthenticationUseCase.loginUser(username, password)
        } catch (e: Exception) {
            handleFailure(e.message ?: "Unknown error occurred")
            null
        }
    }

    private fun handleFailure(message: String) {
        printer.showMessage(message)
    }

    override suspend fun launchUi(user: User?) {
        authenticateUser()
    }
}
