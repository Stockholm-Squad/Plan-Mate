package ui.features.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.usecase.user.CreateUserUseCase
import org.example.ui.features.user.CreateUserUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateUserUiTest {
    private val mockCreateUserUseCase: CreateUserUseCase = mockk(relaxed = true)
    private val mockPrinter: OutputPrinter = mockk(relaxed = true)
    private val mockInputReader: InputReader = mockk()
    private lateinit var createUserUi: CreateUserUi

    @BeforeEach
    fun setUp() {
        createUserUi = CreateUserUi(mockCreateUserUseCase, mockPrinter, mockInputReader)
    }

    @Test
    fun `should show proper messages when launched`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        createUserUi.launchUi()

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
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }

    @Test
    fun `should show error when password is empty`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("username", "")

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }

    @Test
    fun `should show error when both inputs are null`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }


    @Test
    fun `should show success message when user added successfully`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.createUser(username, password) } returns Result.success(true)

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("✅ User $username added successfully!") }
    }

    @Test
    fun `should show error message when user addition fails`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.createUser(username, password) } returns Result.success(false)

        // When
        createUserUi.launchUi()

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
        every { mockCreateUserUseCase.createUser(username, password) } returns
                Result.failure(IllegalArgumentException(errorMessage))

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessage("Error: $errorMessage") }
    }


    @Test
    fun `should call addUserUseCase with correct parameters`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.createUser(username, password) } returns Result.success(true)

        // When
        createUserUi.launchUi()

        // Then
        verify { mockCreateUserUseCase.createUser(username, password) }
    }

    @Test
    fun `should reject null username`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, "validPass")

        // When
        createUserUi.launchUi()

        // Then
        verify(exactly = 0) { mockCreateUserUseCase.createUser(any(), any()) }
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }
    @Test
    fun `should reject null password`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("validUser", null)

        // When
        createUserUi.launchUi()

        // Then
        verify(exactly = 0) { mockCreateUserUseCase.createUser(any(), any()) }
        verify { mockPrinter.showMessage("❌ Error: Username and password cannot be empty") }
    }

}