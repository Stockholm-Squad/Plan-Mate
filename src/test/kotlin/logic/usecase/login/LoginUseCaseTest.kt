package logic.usecase.login

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.*
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.HashingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class LoginUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var validateUserDataUseCase: ValidateUserDataUseCase
    private lateinit var hashingService: HashingService
    private lateinit var useCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        validateUserDataUseCase = mockk(relaxed = true)
        hashingService = mockk(relaxed = true)
        useCase = LoginUseCase(repository, hashingService, validateUserDataUseCase)
    }

    @Test
    fun `loginUser() should return failure when username is empty`() = runTest {
        // Given
        val username = ""
        val password = "password"
        coEvery { repository.getAllUsers() } throws InvalidUserNameException()
        //When & Then
        assertThrows<InvalidUserNameException> {
            useCase.loginUser(
                username = username,
                password = password
            )
        }
        coVerify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when password is empty`() = runTest {
        coEvery { validateUserDataUseCase.isValidPassword(any()) } throws InvalidPasswordException()
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true

        assertThrows<InvalidPasswordException> {
            useCase.loginUser(
                username = "username",
                password = ""
            )
        }
        coVerify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when username starts with a number`() = runTest {
        assertThrows<InvalidUserNameException> {
            useCase.loginUser(
                username = "1john",
                password = "password"
            )
        }
        coVerify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when username is less than 4 characters`() = runTest {
        assertThrows<InvalidUserNameException> {
            useCase.loginUser(
                username = "abc",
                password = "password"
            )
        }
        coVerify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when username is more than 20 characters`() = runTest {
        assertThrows<InvalidUserNameException> {
            useCase.loginUser(
                username = "averyverylongusernamethatexceeds20",
                password = "password"
            )
        }
        coVerify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when password is less than 8 characters`() = runTest {
        //Given
        coEvery { validateUserDataUseCase.isValidPassword(any()) } throws InvalidPasswordException()
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        assertThrows<InvalidPasswordException> {
            useCase.loginUser(
                username = "validUser",
                password = "short"
            )
        }
        coVerify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when user does not exist`() = runTest {
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        coEvery { repository.getUserByUsername(any()) } throws UserDoesNotExistException()

        assertThrows<UserDoesNotExistException> {
            useCase.loginUser(
                username = "Rodina",
                password = "rodinapassword"
            )
        }
        coVerify(exactly = 0) { repository.loginUser(any()) }
    }

    @Test
    fun `loginUser() should return failure when user exist and incorrect password `() = runTest {
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        coEvery { repository.getUserByUsername(any()) } returns User(username =  "johnDoe", hashedPassword = "pass")

        assertThrows<IncorrectPasswordException> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            )
        }
        coVerify(exactly = 0) { repository.loginUser(any()) }
    }

    @Test
    fun `loginUser() should return user when user exist and correct password `() = runTest {
        val user = mockk<org.example.logic.entities.User>()
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true

        val result = useCase.loginUser(
            "johnDoe",
            "hashedPass1"
        )

        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `loginUser() should return failure when username and password are correct and user not exist `() = runTest {
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        coEvery { repository.loginUser(any()) } throws UserDoesNotExistException()

        assertThrows<UserDoesNotExistException> {
            useCase.loginUser(
                "dyjftfhjgbubhkiki",
                "sdfghgbjknllml"
            )
        }
    }

    @Test
    fun `loginUser() should return success when user and password are valid`() = runTest {
        val user = mockk<User>()
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        coEvery { repository.loginUser(any()) } just runs

        val result = useCase.loginUser(username = "johnDoe", password = "hashedPass1")
        assertThat(result).isEqualTo(user)
        coVerify(exactly = 1) { repository.loginUser(any()) }
    }

    @Test
    fun `loginUser() should return failure when repo fails`() = runTest {
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        coEvery { repository.loginUser(any()) } throws Throwable()

        assertThrows<Throwable> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            )
        }
        coVerify(exactly = 1) { repository.loginUser(any()) }
    }

    @Test
    fun `loginUser() should return failure when users are empty`() = runTest {
        coEvery { validateUserDataUseCase.isValidPassword(any()) } returns true
        coEvery { validateUserDataUseCase.isValidUserName(any()) } returns true
        coEvery { repository.loginUser(any()) } throws UsersDataAreEmptyException()

        assertThrows<UsersDataAreEmptyException> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            )
        }
        coVerify(exactly = 1) { repository.loginUser(any()) }
    }

    @Test
    fun `loginOut() should return null when current user is empty`() = runTest {
        coEvery { repository.getCurrentUser() } returns null
        useCase.logout()
        assertThat(useCase.getCurrentUser()).isEqualTo(null)
    }

    @Test
    fun `isUserExist() should return false when current user is empty`() = runTest {
        coEvery { repository.getUserByUsername(any()) } throws UserDoesNotExistException()

        try {
            useCase.isUserExist("Ali")
            assertThat(false).isTrue() // Should not reach here
        } catch (e: UserDoesNotExistException) {
            assertThat(true).isTrue()
        }
    }
}