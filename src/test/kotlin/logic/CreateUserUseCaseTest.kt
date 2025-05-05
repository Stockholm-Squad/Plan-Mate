package logic

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.User
import logic.usecase.login.getAllUsers
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.model.exceptions.InvalidPassword
import org.example.logic.model.exceptions.InvalidUserName
import org.example.logic.model.exceptions.UserExist
import org.example.logic.model.exceptions.UsersDataAreEmpty
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.CreateUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class CreateUserUseCaseTest() {
    private lateinit var repository: UserRepository
    private lateinit var useCase: CreateUserUseCase
    private lateinit var validateUserDataUseCase: ValidateUserDataUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        validateUserDataUseCase = mockk()
        useCase = CreateUserUseCase(repository, validateUserDataUseCase)
    }

    @Test
    fun `addUser() should return failure when username is empty`() {
        assertThrows<InvalidUserNameException> {
            useCase.createUser(
                username = "",
                password = "password"
            ).getOrThrow()
        }

    }

    @Test
    fun `addUser() should return failure when password is empty`() {
        assertThrows<InvalidPasswordException> {
            useCase.createUser(
                username = "username",
                password = ""
            ).getOrThrow()
        }
    }

    @Test
    fun `addUser() should return failure when username starts with a number`() {
        assertThrows<InvalidUserNameException> {
            useCase.createUser(
                username = "1john",
                password = "password"
            ).getOrThrow()
        }
    }

    @Test
    fun `addUser() should return failure when username is less than 4 characters`() {
        assertThrows<InvalidUserNameException> {
            useCase.createUser(
                username = "abc",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when username is more than 20 characters`() {
        assertThrows<InvalidUserNameException> {
            useCase.createUser(
                username = "averyverylongusernamethatexceeds20",
                password = "password"
            ).getOrThrow()
        }
        verify(exactly = 0) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when password is less than 8 characters`() {
        assertThrows<InvalidPasswordException> {
            useCase.createUser(
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
            User(username = "otherUser1", hashedPassword = "hash1"),
            User(username = existingUsername, hashedPassword = "hash2"),
            User(username = "otherUser2", hashedPassword = "hash3")
        )

        // When & Then
        assertThrows<UserExistException> {
            useCase.checkUserExists(users, existingUsername)
        }
    }

    @Test
    fun `addUser() should return success when user and password are valid`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.success(users)
        val result = useCase.createUser(username = "johnDoe", password = "hashedPass1")
        assertThat(result.getOrThrow()).isEqualTo(true)
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when repo fails`() {
        val users = getAllUsers()
        every { repository.getAllUsers() } returns Result.failure(Throwable())
        assertThrows<Throwable> {
            useCase.createUser(
                username = "johnDoe",
                password = "password2"
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }

    @Test
    fun `addUser() should return failure when users are empty`() {
        every { repository.getAllUsers() } returns Result.failure(UsersDataAreEmptyException())
        assertThrows<UsersDataAreEmptyException> {
            repository.getAllUsers(
            ).getOrThrow()
        }
        verify(exactly = 1) { repository.getAllUsers() }
    }
}