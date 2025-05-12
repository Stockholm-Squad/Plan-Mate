package logic.usecase.login

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import logic.usecase.validation.ValidateUserDataUseCase
import modle.buildUser
import org.example.logic.*
import org.example.logic.entities.UserRole
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
        useCase = LoginUseCase(repository, validateUserDataUseCase, hashingService)
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
        val users = getAllUsers()
        coEvery { repository.getAllUsers() } returns users
        assertThrows<UserDoesNotExistException> {
            useCase.loginUser(
                username = "Rodina",
                password = "rodinapassword"
            )
        }
        coVerify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when user exist and incorrect password `() = runTest {
        val users = getAllUsers()
        coEvery { repository.getAllUsers() } returns users
        assertThrows<IncorrectPasswordException> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            )
        }
        coVerify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return success when user and password are valid`() = runTest {
        val users = getAllUsers()
        coEvery { repository.getAllUsers() } returns users
        val user =
            buildUser(
                username = "johnDoe",
                hashedPassword = "6c6b8a98fc1503009200747f9ca0420e",
                userRole = UserRole.MATE
            )
        val result = useCase.loginUser(username = "johnDoe", password = "hashedPass1")
        assertThat(result).isEqualTo(user)
        coVerify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when repo fails`() = runTest {
        getAllUsers()
        coEvery { repository.getAllUsers() } throws Throwable()
        assertThrows<Throwable> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            )
        }
        coVerify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when users are empty`() = runTest {
        coEvery { repository.getAllUsers() } throws UsersDataAreEmptyException()
        assertThrows<UsersDataAreEmptyException> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            )
        }
        coVerify(exactly = 1) { repository.getAllUsers() }
    }
}