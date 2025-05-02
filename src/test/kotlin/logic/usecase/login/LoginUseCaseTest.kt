package logic.usecase.login

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Role
import modle.buildUser
import org.example.logic.model.exceptions.*
import org.example.logic.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class LoginUseCaseTest() {
    private lateinit var repository: UserRepository
    private lateinit var useCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = LoginUseCase(repository)
    }

    @Test
    fun `loginUser() should return failure when username is empty`() {
        assertThrows<InvalidUserName> {
            useCase.loginUser(
                username = "",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when password is empty`() {
        assertThrows<InvalidPassword> {
            useCase.loginUser(
                username = "username",
                password = ""
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when username starts with a number`() {
        assertThrows<InvalidUserName> {
            useCase.loginUser(
                username = "1john",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when username is less than 4 characters`() {
        assertThrows<InvalidUserName> {
            useCase.loginUser(
                username = "abc",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when username is more than 20 characters`() {
        assertThrows<InvalidUserName> {
            useCase.loginUser(
                username = "averyverylongusernamethatexceeds20",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when password is less than 8 characters`() {
        assertThrows<InvalidPassword> {
            useCase.loginUser(
                username = "validUser",
                password = "short"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when user does not exist`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        assertThrows<UserDoesNotExist> {
            useCase.loginUser(
                username = "Rodina",
                password = "rodinapassword"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when user exist and incorrect password `() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        assertThrows<IncorrectPassword> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return success when user and password are valid`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        val user =
            buildUser(username = "johnDoe", hashedPassword = "6c6b8a98fc1503009200747f9ca0420e", role = Role.MATE)
        val result = useCase.loginUser(username = "johnDoe", password = "hashedPass1")
        assertThat(result.getOrThrow()).isEqualTo(user)
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when repo fails`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.failure(Throwable())
        assertThrows<Throwable> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `loginUser() should return failure when users are empty`() {
        every { repository.getAllUsers() } returns Result.failure(UsersDataAreEmpty())
        assertThrows<UsersDataAreEmpty> {
            useCase.loginUser(
                username = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }
}