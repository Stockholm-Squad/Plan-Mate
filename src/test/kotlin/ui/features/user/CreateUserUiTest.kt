package ui.features.user

import io.mockk.*
import org.example.logic.usecase.user.AddUserUseCase
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.features.common.utils.UiMessages
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateUserUiTest {

    private lateinit var createUserUi: CreateUserUi
    private lateinit var createUserUseCase: AddUserUseCase
    private lateinit var printer: OutputPrinter
    private lateinit var inputReader: InputReader

    @BeforeEach
    fun setup() {
        createUserUseCase = mockk()
        printer = mockk(relaxed = true)
        inputReader = mockk()
        createUserUi = CreateUserUi(createUserUseCase, printer, inputReader)
    }

    @Test
    fun `launchUi should add user when username and password are valid`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("testUser", "password123")
        coEvery { createUserUseCase.addUser("testUser", "password123") } returns true

        // When
        createUserUi.launchUi()

        // Then
        verifySequence {
            printer.showMessageLine(UiMessages.ADDING_NEW_USER)
            printer.showMessage(UiMessages.USER_NAME_PROMPT)
            printer.showMessage(UiMessages.PASSWORD_PROMPT)
            printer.showMessageLine(UiMessages.USER_ADDED)
        }
    }

    @Test
    fun `launchUi should show failure message when addUser returns false`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("testUser", "password123")
        coEvery { createUserUseCase.addUser("testUser", "password123") } returns false

        // When
        createUserUi.launchUi()

        // Then
        verify {
            printer.showMessageLine(UiMessages.FAILED_TO_ADD_USER)
        }
    }

    @Test
    fun `launchUi should show error when username is null`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf(null, "password123")

        // When
        createUserUi.launchUi()

        // Then
        verify {
            printer.showMessageLine(UiMessages.USERNAME_PASSWORD_CAN_NOT_BE_EMPTY)
        }
        coVerify(exactly = 0) { createUserUseCase.addUser(any(), any()) }
    }

    @Test
    fun `launchUi should show error when password is null`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("testUser", null)

        // When
        createUserUi.launchUi()

        // Then
        verify {
            printer.showMessageLine(UiMessages.USERNAME_PASSWORD_CAN_NOT_BE_EMPTY)
        }
        coVerify(exactly = 0) { createUserUseCase.addUser(any(), any()) }
    }

    @Test
    fun `launchUi should show error when username is empty`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("", "password123")

        // When
        createUserUi.launchUi()

        // Then
        verify {
            printer.showMessageLine(UiMessages.USERNAME_PASSWORD_CAN_NOT_BE_EMPTY)
        }
        coVerify(exactly = 0) { createUserUseCase.addUser(any(), any()) }
    }

    @Test
    fun `launchUi should show error when password is empty`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("testUser", "")

        // When
        createUserUi.launchUi()

        // Then
        verify {
            printer.showMessageLine(UiMessages.USERNAME_PASSWORD_CAN_NOT_BE_EMPTY)
        }
        coVerify(exactly = 0) { createUserUseCase.addUser(any(), any()) }
    }

    @Test
    fun `createUser should catch exception and print error`() {
        // Given
        every { inputReader.readStringOrNull() } returnsMany listOf("testUser", "password123")
        coEvery { createUserUseCase.addUser("testUser", "password123") } throws RuntimeException("Something went wrong")

        // When
        createUserUi.launchUi()

        // Then
        verify {
            printer.showMessageLine("Something went wrong")
        }
    }
}
