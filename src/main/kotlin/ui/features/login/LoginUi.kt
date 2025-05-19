package org.example.ui.features.login

import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.User
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class LoginUi(
    private val loginUseCase: LoginUseCase,
    private val printer: OutputPrinter,
    private var reader: InputReader,
) : UiLauncher {

    override fun launchUi() {
        authenticateUser()
    }

    fun authenticateUser(): User? {
        val user = loginUseCase.getCurrentUser()
        if (user != null) return user

        printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT)
        val username = reader.readStringOrNull() ?: return null

        printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT)
        val password = reader.readStringOrNull() ?: return null

        return runBlocking {
            try {
                loginUseCase.loginUser(username, password)
                loginUseCase.getCurrentUser()
            } catch (e: Exception) {
                handleFailure(e.message ?: UiMessages.UNKNOWN_ERROR)
                null
            }
        }
    }

    fun logout() {
        loginUseCase.logout()
    }

    private fun handleFailure(message: String) {
        printer.showMessageLine(message)
    }
}
