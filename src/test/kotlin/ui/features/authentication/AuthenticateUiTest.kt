package ui.features.authentication

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Role
import modle.buildUser
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.usecase.authentication.AuthenticateUseCase
import org.example.ui.features.authentication.AuthenticateUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test

class AuthenticateUiTest {
    private lateinit var useCase: AuthenticateUseCase
    private lateinit var ui: AuthenticateUi
    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        useCase = mockk(relaxed = true)
        ui = AuthenticateUi(getAuthenticationUseCase = useCase, reader = reader, printer = printer)
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
        password: String
    ) {
        val expectedMessage = PlanMateExceptions.LogicException.InvalidUserName().message ?: ""

        every { reader.readStringOrNull() } returnsMany listOf(username, password)
        every {
            useCase.authUser(
                username = username,
                password = password
            )
        } returns Result.failure(PlanMateExceptions.LogicException.InvalidUserName())
        ui.authenticateUser()
        verify(exactly = 1) { printer.showMessage(expectedMessage) }
    }

    @ParameterizedTest
    @CsvSource(
        "validUser, ''",
        "validUser, short",
    )
    fun `authenticateUser() should print invalid password when invalid password entered`(
        username: String,
        password: String
    ) {
        val expectedMessage = PlanMateExceptions.LogicException.InvalidPassword().message ?: ""

        every { reader.readStringOrNull() } returnsMany listOf(username, password)
        every {
            useCase.authUser(username = username, password = password)
        } returns Result.failure(PlanMateExceptions.LogicException.InvalidPassword())

        ui.authenticateUser()

        verify(exactly = 1) { printer.showMessage(expectedMessage) }
    }

    @Test
    fun `authenticateUser() should print user does not exist when username and password entered with not existing user`() {
        val expectedMessage = PlanMateExceptions.LogicException.UserDoesNotExist().message ?: ""
        every { useCase.authUser(username = "username", password = "password") } returns Result.failure(
            PlanMateExceptions.LogicException.UserDoesNotExist()
        )
        every { reader.readStringOrNull() } returnsMany listOf("username", "password")

        ui.authenticateUser()

        verify(exactly = 1) { printer.showMessage(expectedMessage) }
    }

    @Test
    fun `authenticateUser() should print incorrect password when incorrect password entered with not existing user`() {
        val expectedMessage = PlanMateExceptions.LogicException.IncorrectPassword().message ?: ""
        every { useCase.authUser(username = "username", password = "password") } returns Result.failure(
            PlanMateExceptions.LogicException.IncorrectPassword()
        )
        every { reader.readStringOrNull() } returnsMany listOf("username", "password")

        ui.authenticateUser()

        verify(exactly = 1) { printer.showMessage(expectedMessage) }
    }

    @Test
    fun `authenticateUser() should return user successfully when valid input logs in`() {
        val user = buildUser(
            username = "adminusername",
            hashedPassword = "011a5aee585278f6be5352cd762203df",
            role = Role.MATE
        )
        every { reader.readStringOrNull() } returnsMany listOf("userName", "userNamePassword")
        every {
            useCase.authUser(
                username = "userName",
                password = "userNamePassword"
            )
        } returns Result.success(
            user
        )
        assertThat(ui.authenticateUser()).isEqualTo(user)
    }

    @Test
    fun `authenticateUser() should print invalid message and returns null when username is null`() {
        every { reader.readStringOrNull() } returns null andThen "username"
        assertThat(ui.authenticateUser()).isEqualTo(null)
        verify { printer.showMessage("Invalid input") }
    }
    @Test
    fun `authenticateUser() should print invalid message and returns null when password is null`() {
        every { reader.readStringOrNull() } returns "username" andThen null
        assertThat(ui.authenticateUser()).isEqualTo(null)
        verify { printer.showMessage("Invalid input") }
    }
}