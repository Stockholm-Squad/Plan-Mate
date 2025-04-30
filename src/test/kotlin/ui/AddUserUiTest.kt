package ui

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.User
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.user.AddUserUseCase
import org.example.ui.features.user.AddUserUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddUserUITest {

    private lateinit var addUserUI: AddUserUi
    private lateinit var addUserUseCase: AddUserUseCase
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        addUserUseCase = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        addUserUI = AddUserUi(addUserUseCase, printer)
    }

    @Test
    fun `addUser should print header and success message when successful`() {
        // Given
        val user = User("john_doe", "hashedPassword123")
        every { addUserUseCase.addUser(user) } returns Result.success(true)

        // When
        addUserUI.addUser(user)

        // Then
        verify {
            printer.showMessage("➕ Adding new user...")
            printer.showMessage("✅ User ${user.username} added successfully!")
        }
    }

    @Test
    fun `addUser should print error message when failure occurs`() {
        // Given
        val user = User("john_doe", "hashedPassword123")
        val errorMessage = "Username already exists"
        every { addUserUseCase.addUser(user) } returns Result.failure(RuntimeException(errorMessage))

        // When
        addUserUI.addUser(user)

        // Then
        verify {
            printer.showMessage("➕ Adding new user...")
            printer.showMessage("❌ Error: $errorMessage")
        }
    }

    @Test
    fun `addUser should call use case exactly once`() {
        // Given
        val user = User("john_doe", "hashedPassword123")
        every { addUserUseCase.addUser(user) } returns Result.success(true)

        // When
        addUserUI.addUser(user)

        // Then
        verify(exactly = 1) { addUserUseCase.addUser(user) }
    }

    @Test
    fun `addUser should print validation error when username is empty`() {
        // Given
        val invalidUser = User("", "hashedPassword123")
        every { addUserUseCase.addUser(invalidUser) } returns
                Result.failure(IllegalArgumentException("Username cannot be empty"))

        // When
        addUserUI.addUser(invalidUser)

        // Then
        verify {
            printer.showMessage("➕ Adding new user...")
            printer.showMessage("❌ Validation error: Username cannot be empty")
        }
    }

    @Test
    fun `addUser should print validation error when password is empty`() {
        // Given
        val invalidUser = User("john_doe", "")
        every { addUserUseCase.addUser(invalidUser) } returns
                Result.failure(IllegalArgumentException("Password cannot be empty"))

        // When
        addUserUI.addUser(invalidUser)

        // Then
        verify {
            printer.showMessage("➕ Adding new user...")
            printer.showMessage("❌ Validation error: Password cannot be empty")
        }
    }
}