// AddUserUi.kt
package org.example.ui.features.user

import logic.model.entities.User
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.user.AddUserUseCase

class AddUserUi(
    private val addUserUseCase: AddUserUseCase,
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
        if (username != null && password != null) {
            val user = User(username, password)
            addUser(user)
        } else {
            printer.showMessage("❌ Error: Username and password cannot be empty")
        }
    }

    fun addUser(user: User) {
        addUserUseCase.addUser(user)
            .onSuccess { success ->
                if (success) {
                    printer.showMessage("✅ User ${user.username} added successfully!")
                } else {
                    printer.showMessage("❌ Error: Failed to add user")
                }
            }
            .onFailure { error ->
                printer.showMessage("Error: ${error.message}")
            }
    }
}