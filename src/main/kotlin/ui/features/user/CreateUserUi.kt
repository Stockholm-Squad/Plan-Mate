package org.example.ui.features.user

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.user.CreateUserUseCase

class CreateUserUi(
    private val createUserUseCase: CreateUserUseCase,
    private val printer: OutputPrinter,
    private val inputReader: InputReader
) {
    fun launchUi() {
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
}