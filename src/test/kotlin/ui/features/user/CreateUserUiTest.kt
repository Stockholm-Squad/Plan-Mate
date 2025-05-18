package ui.features.user

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.UsersDataAreEmptyException
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.usecase.user.ManageUserUseCase
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateUserUiTest {
    private val mockCreateUserUseCase: ManageUserUseCase = mockk(relaxed = true)
    private val mockPrinter: OutputPrinter = mockk(relaxed = true)
    private val mockInputReader: InputReader = mockk()
    private lateinit var createUserUi: CreateUserUi
    val user = User(
        username = "rodina",
        hashedPassword = "0192023a7bbd73250516f069df18b500",
        id = UUID.fromString("de25ea95-fca4-4f87-90e8-fd9c6aa0a716"),
        userRole = UserRole.ADMIN
    )

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
            mockPrinter.showMessageLine("➕ Adding new user...")
            mockPrinter.showMessageLine("Enter username:")
            mockPrinter.showMessageLine("Enter password:")
        }
    }

    @Test
    fun `should show error when username is empty`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("", "password")

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

    @Test
    fun `should show error when password is empty`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("username", "")

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

    @Test
    fun `should show error when both inputs are null`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }


    @Test
    fun `should show success message when user added successfully`() = runTest {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        coEvery { mockCreateUserUseCase.addUser(username, password) } returns true

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessageLine("✅ User $username added successfully!") }
    }

    @Test
    fun `should show error message when user addition fails`() = runTest {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        coEvery { mockCreateUserUseCase.addUser(username, password) } returns false

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessageLine("Failed to add user") }
    }

    @Test
    fun `should show error message when exception occurs`() = runTest {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        coEvery { mockCreateUserUseCase.addUser(username, password) } throws UsersDataAreEmptyException()

        // When
        createUserUi.launchUi()

        // Then
        verify { mockPrinter.showMessageLine("${UsersDataAreEmptyException().message}") }
    }


    @Test
    fun `should call addUserUseCase with correct parameters`() = runTest {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        coEvery { mockCreateUserUseCase.addUser(username, password) } returns true

        // When
        createUserUi.launchUi()


        // Then
        coVerify { mockCreateUserUseCase.addUser(username, password) }
    }

    @Test
    fun `should reject null username`() = runTest {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, "validPass")

        // When
        createUserUi.launchUi()

        // Then
        coVerify(exactly = 0) { mockCreateUserUseCase.addUser(any(), any()) }
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

    @Test
    fun `should reject null password`() = runTest {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("validUser", null)

        // When
        createUserUi.launchUi()

        // Then
        coVerify(exactly = 0) { mockCreateUserUseCase.addUser(any(), any()) }
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

}