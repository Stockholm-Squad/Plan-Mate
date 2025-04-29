package ui

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.user.AddUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildUser

class AddUserUITest {

    private lateinit var addUserUI: AddUserUI
    private lateinit var addUserUseCase: AddUserUseCase
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        addUserUseCase = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        addUserUI = AddUserUI(addUserUseCase, printer)
    }

    @Test
    fun `addUser should print header and success message when successful`() {
        // Given
        val user = buildUser(1, "John Doe", "john@example.com")
        every { addUserUseCase.addUser(user) } returns Result.success(user)

        // When
        addUserUI.addUser(user)

        // Then
        verify {
            printer.printLine("➕ Adding new user...")
            printer.printLine("✅ User ${user.name} added successfully!")
            printer.printUser(user) // Assuming you have a printUser method
        }
    }

    @Test
    fun `addUser should print error message when failure occurs`() {
        // Given
        val user = buildUser(1, "John Doe", "john@example.com")
        val errorMessage = "Email already exists"
        every { addUserUseCase.addUser(user) } returns Result.failure(RuntimeException(errorMessage))

        // When
        addUserUI.addUser(user)

        // Then
        verify {
            printer.printLine("➕ Adding new user...")
            printer.printLine("❌ Error: $errorMessage")
        }
    }

    @Test
    fun `addUser should call use case exactly once`() {
        // Given
        val user = buildUser(1, "John Doe", "john@example.com")
        every { addUserUseCase.addUser(user) } returns Result.success(user)

        // When
        addUserUI.addUser(user)

        // Then
        verify(exactly = 1) { addUserUseCase.addUser(user) }
    }

    @Test
    fun `addUser should print validation error when user data is invalid`() {
        // Given
        val invalidUser = buildUser(1, "", "invalid-email")
        every { addUserUseCase.addUser(invalidUser) } returns
                Result.failure(IllegalArgumentException("Name cannot be empty"))

        // When
        addUserUI.addUser(invalidUser)

        // Then
        verify {
            printer.printLine("➕ Adding new user...")
            printer.printLine("❌ Validation error: Name cannot be empty")
        }
    }
}