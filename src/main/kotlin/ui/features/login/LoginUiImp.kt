package org.example.ui.features.login

import kotlinx.coroutines.runBlocking
import org.example.logic.entities.User
import logic.usecase.login.LoginUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class LoginUiImp(
    private val loginUseCase: LoginUseCase,
    private val printer: OutputPrinter,
    private var reader: InputReader
) : LoginUi {

    override fun authenticateUser(): User? {
        val user = loginUseCase.getCurrentUser()
        if (user != null) return user

        printer.showMessageLine("Please enter your user name: ")
        val username = reader.readStringOrNull() ?: return null

        printer.showMessageLine("Please enter your Password: ")
        val password = reader.readStringOrNull() ?: return null

        return runBlocking {
            try {
                loginUseCase.loginUser(username, password)
            } catch (e: Exception) {
                handleFailure(e.message ?: "Unknown error occurred")
                null
            }
        }
    }

    override fun logout() {
        loginUseCase.logout()
    }

    private fun handleFailure(message: String) {
        printer.showMessageLine(message)
    }

    override fun launchUi() {
        authenticateUser()
    }
}
