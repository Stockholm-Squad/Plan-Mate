package ui.features.user

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
    private val mockAddUserUseCase: AddUserUseCase = mockk(relaxed = true)
    private val mockPrinter: OutputPrinter = mockk(relaxed = true)
    private val mockInputReader: InputReader = mockk()
    private lateinit var addUserUi: AddUserUi

    @BeforeEach
    fun setUp() {
        addUserUi = AddUserUi(mockAddUserUseCase, mockPrinter, mockInputReader)
    }

    @Test
    fun `should show proper messages when launched`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        addUserUi.launchUi()

        // Then
        verify {
            mockPrinter.showMessage("➕ Adding new user...")
            mockPrinter.showMessage("Enter username:")
            mockPrinter.showMessage("Enter password:")
        }
    }

    @Test
    fun `should show error when username is empty`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("", "password")

        // When
        addUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }

    @Test
    fun `should show error when password is empty`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("username", "")

        // When
        addUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }

    @Test
    fun `should show error when both inputs are null`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        addUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }


    @Test
    fun `should show success message when user added successfully`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockAddUserUseCase.addUser(username, password) } returns Result.success(true)

        // When
        addUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("✅ User $username added successfully!") }
    }

    @Test
    fun `should show error message when user addition fails`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockAddUserUseCase.addUser(username, password) } returns Result.success(false)

        // When
        addUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Failed to add user") }
    }

    @Test
    fun `should show error message when exception occurs`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        val errorMessage = "Invalid username format"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockAddUserUseCase.addUser(username, password) } returns
                Result.failure(IllegalArgumentException(errorMessage))

        // When
        addUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("Error: $errorMessage") }
    }


    @Test
    fun `should call addUserUseCase with correct parameters`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockAddUserUseCase.addUser(username, password) } returns Result.success(true)

        // When
        addUserUi.launchUi()

        // Then
        verify { mockAddUserUseCase.addUser(username, password) }
    }

    @Test
    fun `should reject null username`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, "validPass")

        // When
        addUserUi.launchUi()

        // Then
        verify(exactly = 0) { mockAddUserUseCase.addUser(any(), any()) }
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }
    @Test
    fun `should reject null password`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("validUser", null)

        // When
        addUserUi.launchUi()

        // Then
        verify(exactly = 0) { mockAddUserUseCase.addUser(any(), any()) }
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }

}