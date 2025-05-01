package logic

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.User
import logic.usecase.authentication.getAllUsers
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.AddUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class AddUserUseCaseTest() {
    private lateinit var repository: UserRepository
    private lateinit var useCase: AddUserUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = AddUserUseCase(repository)
    }

    @Test
    fun `addUser() should return failure when username is empty`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.addUser(
                username = "",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when password is empty`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidPassword> {
            useCase.addUser(
                username = "username",
                password = ""
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when username starts with a number`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.addUser(
                username = "1john",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when username is less than 4 characters`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.addUser(
                username = "abc",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when username is more than 20 characters`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidUserName> {
            useCase.addUser(
                username = "averyverylongusernamethatexceeds20",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when password is less than 8 characters`() {
        assertThrows<PlanMateExceptions.LogicException.InvalidPassword> {
            useCase.addUser(
                username = "validUser",
                password = "short"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `checkUserExists should throw UserExist when username exists in list`() {
        // Given
        val existingUsername = "existingUser"
        val users = listOf(
            User("otherUser1", "hash1"),
            User(existingUsername, "hash2"),
            User("otherUser2", "hash3")
        )

        // When & Then
        assertThrows<PlanMateExceptions.LogicException.UserExist> {
            useCase.checkUserExists(users, existingUsername)
        }
    }

    @Test
    fun `addUser() should return success when user and password are valid`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        val result = useCase.addUser(username = "johnDoe", password = "hashedPass1")
        assertThat(result.getOrThrow()).isEqualTo(true)
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when repo fails`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.failure(Throwable())
        assertThrows<Throwable> {
            useCase.addUser(
                username = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when users are empty`() {
        every { repository.getAllUsers() } returns Result.failure(PlanMateExceptions.LogicException.UsersIsEmpty())
        assertThrows<PlanMateExceptions.LogicException.UsersIsEmpty> {
            repository.getAllUsers(
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }
}