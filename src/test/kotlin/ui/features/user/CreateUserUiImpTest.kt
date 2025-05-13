package ui.features.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.User
import logic.model.entities.UserRole
import org.example.logic.model.exceptions.UsersDataAreEmpty
import org.example.logic.usecase.extention.toSafeUUID
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.usecase.user.AddUserUseCase
import org.example.ui.features.user.CreateUserUiImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateUserUiImpTest {
    private val mockCreateUserUseCase: AddUserUseCase = mockk(relaxed = true)
    private val mockPrinter: OutputPrinter = mockk(relaxed = true)
    private val mockInputReader: InputReader = mockk()
    private lateinit var createUserUiImp: CreateUserUiImp
    val user = User(
        username = "rodina",
        hashedPassword = "0192023a7bbd73250516f069df18b500",
        id = "de25ea95-fca4-4f87-90e8-fd9c6aa0a716".toSafeUUID(),
        userRole = UserRole.ADMIN
    )

    @BeforeEach
    fun setUp() {
        createUserUiImp = CreateUserUiImp(mockCreateUserUseCase, mockPrinter, mockInputReader)

    }

    @Test
    fun `should show proper messages when launched`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        createUserUiImp.launchUi(user)

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
        createUserUiImp.launchUi(user)

        // Then
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

    @Test
    fun `should show error when password is empty`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("username", "")

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

    @Test
    fun `should show error when both inputs are null`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, null)

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }


    @Test
    fun `should show success message when user added successfully`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.addUser(username, password) } returns Result.success(true)

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify { mockPrinter.showMessageLine("✅ User $username added successfully!") }
    }

    @Test
    fun `should show error message when user addition fails`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.addUser(username, password) } returns Result.success(false)

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify { mockPrinter.showMessageLine("Failed to add user") }
    }

    @Test
    fun `should show error message when exception occurs`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.addUser(username, password) } returns
                Result.failure(UsersDataAreEmpty())

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify { mockPrinter.showMessageLine("${UsersDataAreEmpty().message}") }
    }


    @Test
    fun `should call addUserUseCase with correct parameters`() {
        // Given
        val username = "testUser"
        val password = "testPass123"
        every { mockInputReader.readStringOrNull() } returnsMany listOf(username, password)
        every { mockCreateUserUseCase.addUser(username, password) } returns Result.success(true)

        // When
        createUserUiImp.launchUi(
            user
        )


        // Then
        verify { mockCreateUserUseCase.addUser(username, password) }
    }

    @Test
    fun `should reject null username`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf(null, "validPass")

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify(exactly = 0) { mockCreateUserUseCase.addUser(any(), any()) }
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

    @Test
    fun `should reject null password`() {
        // Given
        every { mockInputReader.readStringOrNull() } returnsMany listOf("validUser", null)

        // When
        createUserUiImp.launchUi(user)

        // Then
        verify(exactly = 0) { mockCreateUserUseCase.addUser(any(), any()) }
        verify { mockPrinter.showMessageLine("Username and password cannot be empty") }
    }

}