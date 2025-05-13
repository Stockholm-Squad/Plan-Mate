package org.example.ui.features.user

import kotlinx.coroutines.runBlocking
import org.example.logic.usecase.user.AddUserUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class CreateUserUiImp(
    private val addUserUseCase: AddUserUseCase,
    private val printer: OutputPrinter,
    private val inputReader: InputReader
) : CreateUserUi {

    private fun createUser(username: String, password: String) = runBlocking {
        try {
            addUserUseCase.addUser(username, password).also { isSuccess ->
                if (isSuccess) {
                    printer.showMessage("✅ User $username added successfully!")
                } else {
                    printer.showMessage("Failed to add user")
                }
            }
        } catch (exception: Exception) {
            printer.showMessage(exception.message ?: "Failed to add user")
        }
    }

    override fun launchUi() {
        printer.showMessage("➕ Adding new user...")

        printer.showMessage("Enter username:")
        val username = inputReader.readStringOrNull()

        printer.showMessage("Enter password:")
        val password = inputReader.readStringOrNull()

        runBlocking {
            if (username?.isNotEmpty() == true && password?.isNotEmpty() == true) {
                createUser(username, password)
            } else {
                printer.showMessage( "Username and password cannot be empty")
            }
        }
    }
}