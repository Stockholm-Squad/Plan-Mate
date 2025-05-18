package ui.features.login

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import modle.buildUser
import org.example.logic.IncorrectPasswordException
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.UserRole
import org.example.ui.features.login.LoginUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test

class LoginUiTest {
    private lateinit var useCase: LoginUseCase
    private lateinit var ui: LoginUi
    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        useCase = mockk(relaxed = true)
        ui = LoginUi(loginUseCase = useCase, reader = reader, printer = printer)
    }

    @ParameterizedTest
    @CsvSource(
        "'', validPassword123",
        "1john, validPassword123",
        "abc, validPassword123",
        "averyverylongusernamethatexceeds20, validPassword123"
    )
    fun `authenticateUser() should print invalid username when invalid username entered`(
        username: String,
        password: String,
    ) = runTest {
        val expectedMessage = InvalidUserNameException().message ?: ""

        every { reader.readStringOrNull() } returnsMany listOf(username, password)
        coEvery {
            useCase.loginUser(
                username = username,
                password = password
            )
        } throws InvalidUserNameException()
        ui.authenticateUser()
        verify(exactly = 1) { printer.showMessageLine(expectedMessage) }
    }

    @ParameterizedTest
    @CsvSource(
        "validUser, ''",
        "validUser, short",
    )
    fun `authenticateUser() should print invalid password when invalid password entered`(
        username: String,
        password: String,
    ) = runTest {
        val expectedMessage = InvalidPasswordException().message ?: ""

        every { reader.readStringOrNull() } returnsMany listOf(username, password)
        coEvery {
            useCase.loginUser(username = username, password = password)
        } throws InvalidPasswordException()

        ui.authenticateUser()

        verify(exactly = 1) { printer.showMessageLine(expectedMessage) }
    }

    @Test
    fun `authenticateUser() should print user does not exist when username and password entered with not existing user`() =
        runTest {
            val expectedMessage = UserDoesNotExistException().message ?: ""
            coEvery {
                useCase.loginUser(
                    username = "username",
                    password = "password"
                )
            } throws UserDoesNotExistException()
            every { reader.readStringOrNull() } returnsMany listOf("username", "password")

            ui.authenticateUser()

            verify(exactly = 1) { printer.showMessageLine(expectedMessage) }
        }

    @Test
    fun `authenticateUser() should print incorrect password when incorrect password entered with not existing user`() =
        runTest {
            val expectedMessage = IncorrectPasswordException().message ?: ""
            coEvery {
                useCase.loginUser(
                    username = "username",
                    password = "password"
                )
            } throws IncorrectPasswordException()
            every { reader.readStringOrNull() } returnsMany listOf("username", "password")

            ui.authenticateUser()

            verify(exactly = 1) { printer.showMessageLine(expectedMessage) }
        }

    @Test
    fun `authenticateUser() should return user successfully when valid input logs in`() = runTest {
        val user = buildUser(
            username = "adminusername",
            hashedPassword = "011a5aee585278f6be5352cd762203df",
            userRole = UserRole.MATE
        )
        every { reader.readStringOrNull() } returnsMany listOf("userName", "userNamePassword")
        coEvery {
            useCase.loginUser(
                username = "userName",
                password = "userNamePassword"
            )
        } returns user
        assertThat(ui.authenticateUser()).isEqualTo(user)
    }

    @Test
    fun `authenticateUser() should print invalid message and returns null when username is null`() {
        every { reader.readStringOrNull() } returns null andThen "username"
        assertThat(ui.authenticateUser()).isEqualTo(null)
    }

    @Test
    fun `authenticateUser() should print invalid message and returns null when password is null`() {
        every { reader.readStringOrNull() } returns "username" andThen null
        assertThat(ui.authenticateUser()).isEqualTo(null)
    }
}