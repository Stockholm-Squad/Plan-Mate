package org.example.ui.features.user

import logic.models.entities.User
import org.example.logic.usecase.user.CreateUserUseCase
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class CreateUserUiImp(
    private val createUserUseCase: CreateUserUseCase,
    private val printer: OutputPrinter,
    private val inputReader: InputReader
):CreateUserUi {

    private fun createUser(username: String, password: String) {
        createUserUseCase.createUser(username, password)
            .onSuccess { success ->
                if (success) {
                    printer.showMessage("✅ User ${username} added successfully!")
                } else {
                    printer.showMessage("❌ Error: Failed to add user")
                }
            }
            .onFailure { error ->
                printer.showMessage("Error: ${error.message}")
            }
    }

    override fun launchUi(user: User?) {
        printer.showMessage("➕ Adding new user...")
        // Get username input
        printer.showMessage("Enter username:")
        val username = inputReader.readStringOrNull()
        // Get password input
        printer.showMessage("Enter password:")
        val password = inputReader.readStringOrNull()

        // Create user if both inputs are valid
        if (username?.isNotEmpty() == true && password?.isNotEmpty() == true) {
            createUser(username, password)
        } else {
            printer.showMessage("❌ Error: Username and password cannot be empty")
        }
    }
}