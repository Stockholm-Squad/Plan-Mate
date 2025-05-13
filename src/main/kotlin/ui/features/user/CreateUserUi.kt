package org.example.ui.features.user

import kotlinx.coroutines.runBlocking
import org.example.logic.usecase.user.AddUserUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class CreateUserUi(
    private val createUserUseCase: AddUserUseCase,
    private val printer: OutputPrinter,
    private val inputReader: InputReader,
) : UiLauncher {

    override fun launchUi() {
        printer.showMessageLine(UiMessages.ADDING_NEW_USER)

        printer.showMessage(UiMessages.USER_NAME_PROMPT)
        val username = inputReader.readStringOrNull()

        printer.showMessage(UiMessages.PASSWORD_PROMPT)
        val password = inputReader.readStringOrNull()

        runBlocking {
            if (username?.isNotEmpty() == true && password?.isNotEmpty() == true) {
                createUser(username, password)
            } else {
                printer.showMessageLine(UiMessages.USERNAME_PASSWORD_CAN_NOT_BE_EMPTY)
            }
        }
    }

    private fun createUser(username: String, password: String) = runBlocking {
        try {
            createUserUseCase.addUser(username, password).also { isSuccess ->
                if (isSuccess) {
                    printer.showMessageLine(UiMessages.USER_ADDED)
                } else {
                    printer.showMessageLine(UiMessages.FAILED_TO_ADD_USER)
                }
            }
        } catch (exception: Exception) {
            printer.showMessageLine(exception.message ?: UiMessages.FAILED_TO_ADD_USER)
        }
    }
}