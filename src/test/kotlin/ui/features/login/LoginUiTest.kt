package ui.features.login

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.usecase.login.LoginUseCase
import org.example.logic.IncorrectPasswordException
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.User
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.login.LoginUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class LoginUiTest {
    private lateinit var loginUi: LoginUi
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var printer: OutputPrinter
    private lateinit var reader: InputReader

    @BeforeEach
    fun setUp() {
        loginUseCase = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        loginUi = LoginUi(loginUseCase, printer, reader)
    }

    @Test
    fun `launchUi should attempt authentication when no user is logged in`() {
        // Given
        val user = mockk<User> {
            every { id } returns UUID.randomUUID()
            every { username } returns "testUser"
        }
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } just runs
        coEvery { loginUseCase.getCurrentUser() } returns user

        // When
        loginUi.launchUi()

        // Then
        verify { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) }
        verify { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) }
        coVerify { loginUseCase.loginUser("testUser", "password") }
    }

    @Test
    fun `authenticateUser should return existing user when user is already logged in`() {
        // Given
        val user = mockk<User> {
            every { id } returns UUID.randomUUID()
            every { username } returns "testUser"
        }
        every { loginUseCase.getCurrentUser() } returns user

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isEqualTo(user)
        verify(exactly = 0) { printer.showMessage(any()) }
        verify(exactly = 0) { reader.readStringOrNull() }
    }

    @Test
    fun `authenticateUser should return null when username input is null`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returns null
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
        verify { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) }
        verify(exactly = 0) { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) }
        coVerify(exactly = 0) { loginUseCase.loginUser(any(), any()) }
    }

    @Test
    fun `authenticateUser should return null when password input is null`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", null)
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
        verify { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) }
        verify { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) }
        coVerify(exactly = 0) { loginUseCase.loginUser(any(), any()) }
    }

    @Test
    fun `authenticateUser should return user when login is successful`() {
        // Given
        val user = mockk<User> {
            every { id } returns UUID.randomUUID()
            every { username } returns "testUser"
        }
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } just runs
        coEvery { loginUseCase.getCurrentUser() } returns user

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isEqualTo(user)
        verify { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) }
        verify { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) }
        coVerify { loginUseCase.loginUser("testUser", "password") }
    }

    @Test
    fun `authenticateUser should return null and show error when login fails with InvalidUserNameException`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        every { printer.showMessageLine("Invalid username") } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } throws InvalidUserNameException()

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
        verify { printer.showMessageLine("Invalid username") }
    }

    @Test
    fun `authenticateUser should return null and show error when login fails with InvalidPasswordException`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        every { printer.showMessageLine("Invalid password") } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } throws InvalidPasswordException()

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
        verify { printer.showMessageLine("Invalid password") }
    }

    @Test
    fun `authenticateUser should return null and show error when login fails with IncorrectPasswordException`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        every { printer.showMessageLine("Incorrect password") } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } throws IncorrectPasswordException()

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
        verify { printer.showMessageLine("Incorrect password") }
    }

    @Test
    fun `authenticateUser should return null and show error when login fails with UserDoesNotExistException`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        every { printer.showMessageLine("User does not exist") } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } throws UserDoesNotExistException()

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
        verify { printer.showMessageLine("User does not exist") }
    }

    @Test
    fun `authenticateUser should return null and show unknown error when login fails with null message`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null
        every { reader.readStringOrNull() } returnsMany listOf("testUser", "password")
        every { printer.showMessage(UiMessages.LOGIN_USER_NAME_PROMPT) } returns Unit
        every { printer.showMessage(UiMessages.LOGIN_PASSWORD_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.UNKNOWN_ERROR) } returns Unit
        coEvery { loginUseCase.loginUser("testUser", "password") } throws Exception("")

        // When
        val result = loginUi.authenticateUser()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `logout should call logout on loginUseCase`() {
        // Given
        every { loginUseCase.logout() } returns Unit

        // When
        loginUi.logout()

        // Then
        verify { loginUseCase.logout() }
    }
}