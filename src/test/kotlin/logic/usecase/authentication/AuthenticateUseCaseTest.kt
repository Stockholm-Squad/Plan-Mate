package logic.usecase.authentication

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Role
import modle.buildUser
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.authentication.AuthenticateUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class AuthenticateUseCaseTest() {
    private lateinit var repository: UserRepository
    private lateinit var useCase: AuthenticateUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = AuthenticateUseCase(repository)
    }

    @Test
    fun `authUser() should return failure when username is empty`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.authUser(
                userName = "",
                password = "password"
            )
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when password is empty`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidPassword> {
            useCase.authUser(
                userName = "username",
                password = ""
            )
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when username starts with a number`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.authUser(
                userName = "1john",
                password = "password"
            )
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when username is less than 4 characters`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.authUser(
                userName = "abc",
                password = "password"
            )
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when username is more than 20 characters`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.authUser(
                userName = "averyverylongusernamethatexceeds20",
                password = "password"
            )
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when password is less than 8 characters`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidPassword> {
            useCase.authUser(
                userName = "validUser",
                password = "short"
            )
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when user does not exist`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        assertThrows<PlanMateExceptions.LogicException.UserDoesNotExist> {
            useCase.authUser(
                userName = "Rodina",
                password = "rodinapassword"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when user exist and incorrect password `() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        assertThrows<PlanMateExceptions.LogicException.IncorrectPassword> {
            useCase.authUser(
                userName = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return success when user and password are valid`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        val user = buildUser(username = "johnDoe", hashedPassword = "hashedPass1", role = Role.MATE)
        assertThat(useCase.authUser(userName = "johnDoe", password = "password").getOrThrow()).isEqualTo(user)
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `authUser() should return failure when repo fails`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.failure(Throwable())
        assertThrows<Throwable> {
            useCase.authUser(
                userName = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }
}