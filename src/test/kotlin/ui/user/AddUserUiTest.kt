// AddUserUiTest.kt
package ui.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.user.AddUserUseCase
import org.example.ui.features.user.AddUserUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddUserUiTest {

    private lateinit var addUserUi: AddUserUi
    private lateinit var addUserUseCase: AddUserUseCase
    private lateinit var printer: OutputPrinter
    private lateinit var inputReader: InputReader

    @BeforeEach
    fun setUp() {
        addUserUseCase = mockk()
        printer = mockk(relaxed = true)
        inputReader = mockk()
        addUserUi = AddUserUi(addUserUseCase, printer, inputReader)
    }

    @Test
    fun `launchUi should show success when user is added`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { inputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { addUserUseCase.addUser(any()) } returns Result.success(true)

        // When
        addUserUi.launchUi()

        // Then
        verify {
            printer.showMessage("✅ User $username added successfully!")
        }
    }

    @Test
    fun `launchUi should show error when addUser returns false`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { inputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { addUserUseCase.addUser(any()) } returns Result.success(false)

        // When
        addUserUi.launchUi()

        // Then
        verify {
            printer.showMessage("❌ Error: Failed to add user")
        }
    }

    @Test
    fun `launchUi should show validation error for invalid input`() {
        // Given
        val username = ""
        val password = "testPass123"
        every { inputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { addUserUseCase.addUser(any()) } returns Result.failure(IllegalArgumentException("Username cannot be empty"))

        // When
        addUserUi.launchUi()

        // Then
        verify {
            printer.showMessage("Error: Username cannot be empty")
        }
    }

    @Test
    fun `launchUi should handle null username`() {
        // Given
        every { inputReader.readStringOrNull() } returns null

        // When
        addUserUi.launchUi()

        // Then
        verify(exactly = 0) { addUserUseCase.addUser(any()) }
        verify {
            printer.showMessage("❌ Error: Username and password cannot be empty")
        }
    }

    @Test
    fun `launchUi should handle null password`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("testUser", null)

        // When
        addUserUi.launchUi()

        // Then
        verify(exactly = 0) { addUserUseCase.addUser(any()) }
        verify {
            printer.showMessage("❌ Error: Username and password cannot be empty")
        }
    }
}