//package ui.features.login
//
//import com.google.common.truth.Truth.assertThat
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import logic.model.entities.UserRole
//import modle.buildUser
//import org.example.ui.input_output.input.InputReader
//import org.example.ui.input_output.output.OutputPrinter
//import org.example.logic.model.exceptions.IncorrectPassword
//import org.example.logic.model.exceptions.UserDoesNotExist
//import logic.usecase.login.LoginUseCase
//import org.example.logic.model.exceptions.InvalidUserName
//import org.example.logic.model.exceptions.InvalidPassword
//import org.example.ui.features.login.LoginUiImp
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.CsvSource
//import kotlin.test.Test
//
//class LoginUiImpTest {
//    private lateinit var useCase: LoginUseCase
//    private lateinit var ui: LoginUiImp
//    private lateinit var reader: InputReader
//    private lateinit var printer: OutputPrinter
//
//    @BeforeEach
//    fun setUp() {
//        reader = mockk(relaxed = true)
//        printer = mockk(relaxed = true)
//        useCase = mockk(relaxed = true)
//        ui = LoginUiImp(getAuthenticationUseCase = useCase, reader = reader, printer = printer)
//    }
//
//    @ParameterizedTest
//    @CsvSource(
//        "'', validPassword123",
//        "1john, validPassword123",
//        "abc, validPassword123",
//        "averyverylongusernamethatexceeds20, validPassword123"
//    )
//    fun `authenticateUser() should print invalid username when invalid username entered`(
//        username: String,
//        password: String
//    ) {
//        val expectedMessage = InvalidUserName().message ?: ""
//
//        every { reader.readStringOrNull() } returnsMany listOf(username, password)
//        every {
//            useCase.loginUser(
//                username = username,
//                password = password
//            )
//        } returns Result.failure(InvalidUserName())
//        ui.authenticateUser()
//        verify(exactly = 1) { printer.showMessage(expectedMessage) }
//    }
//
//    @ParameterizedTest
//    @CsvSource(
//        "validUser, ''",
//        "validUser, short",
//    )
//    fun `authenticateUser() should print invalid password when invalid password entered`(
//        username: String,
//        password: String
//    ) {
//        val expectedMessage = InvalidPassword().message ?: ""
//
//        every { reader.readStringOrNull() } returnsMany listOf(username, password)
//        every {
//            useCase.loginUser(username = username, password = password)
//        } returns Result.failure(InvalidPassword())
//
//        ui.authenticateUser()
//
//        verify(exactly = 1) { printer.showMessage(expectedMessage) }
//    }
//
//    @Test
//    fun `authenticateUser() should print user does not exist when username and password entered with not existing user`() {
//        val expectedMessage = UserDoesNotExist().message ?: ""
//        every { useCase.loginUser(username = "username", password = "password") } returns Result.failure(
//            UserDoesNotExist()
//        )
//        every { reader.readStringOrNull() } returnsMany listOf("username", "password")
//
//        ui.authenticateUser()
//
//        verify(exactly = 1) { printer.showMessage(expectedMessage) }
//    }
//
//    @Test
//    fun `authenticateUser() should print incorrect password when incorrect password entered with not existing user`() {
//        val expectedMessage = IncorrectPassword().message ?: ""
//        every { useCase.loginUser(username = "username", password = "password") } returns Result.failure(
//            IncorrectPassword()
//        )
//        every { reader.readStringOrNull() } returnsMany listOf("username", "password")
//
//        ui.authenticateUser()
//
//        verify(exactly = 1) { printer.showMessage(expectedMessage) }
//    }
//
//    @Test
//    fun `authenticateUser() should return user successfully when valid input logs in`() {
//        val user = buildUser(
//            username = "adminusername",
//            hashedPassword = "011a5aee585278f6be5352cd762203df",
//            userRole = UserRole.MATE
//        )
//        every { reader.readStringOrNull() } returnsMany listOf("userName", "userNamePassword")
//        every {
//            useCase.loginUser(
//                username = "userName",
//                password = "userNamePassword"
//            )
//        } returns Result.success(
//            user
//        )
//        assertThat(ui.authenticateUser()).isEqualTo(user)
//    }
//
//    @Test
//    fun `authenticateUser() should print invalid message and returns null when username is null`() {
//        every { reader.readStringOrNull() } returns null andThen "username"
//        assertThat(ui.authenticateUser()).isEqualTo(null)
//        verify { printer.showMessage("Invalid input") }
//    }
//    @Test
//    fun `authenticateUser() should print invalid message and returns null when password is null`() {
//        every { reader.readStringOrNull() } returns "username" andThen null
//        assertThat(ui.authenticateUser()).isEqualTo(null)
//        verify { printer.showMessage("Invalid input") }
//    }
//}