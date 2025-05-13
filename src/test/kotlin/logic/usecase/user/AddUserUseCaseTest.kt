package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.AddUserUseCase
import org.example.logic.utils.HashingService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AddUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var validateUserDataUseCase: ValidateUserDataUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var hashingService: HashingService
    private lateinit var addUserUseCase: AddUserUseCase
    val userId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        validateUserDataUseCase = mockk()
        loginUseCase = mockk()
        hashingService = mockk()
        addUserUseCase = AddUserUseCase(
            userRepository,
            validateUserDataUseCase,
            loginUseCase,
            hashingService
        )
    }

    @Test
    fun `addUser should return true when user is valid and does not exist`() = runBlocking {
        // Given
        val username = "newUser"
        val password = "StrongPass123"
        val hashed = "hashed_pass"

        every { validateUserDataUseCase.isValidUserName(username) } returns true
        every { validateUserDataUseCase.isValidPassword(password) } returns true
        coEvery { loginUseCase.isUserExist(username) } returns false
        every { hashingService.hash(password) } returns hashed
        coEvery { userRepository.addUser(any()) } returns true

        // When
        val result = addUserUseCase.addUser(username, password)

        // That
        assertThat(result).isTrue()
        coVerify {
            loginUseCase.isUserExist(username)
            userRepository.addUser(User(userId, username, hashed))
        }
    }

    @Test
    fun `addUser should throw InvalidUserNameException when username is invalid`() = runBlocking {
        // Given
        val username = "!"
        val password = "Valid123"

        every { validateUserDataUseCase.isValidUserName(username) } returns false

        // When & Then
        assertThrows(InvalidUserNameException::class.java) {
            runBlocking {
                addUserUseCase.addUser(username, password)
            }
        }

        coVerify(exactly = 0) { loginUseCase.isUserExist(any()) }
        coVerify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `addUser should throw InvalidPasswordException when password is invalid`() = runBlocking {
        // Given
        val username = "validUser"
        val password = "123"

        every { validateUserDataUseCase.isValidUserName(username) } returns true
        every { validateUserDataUseCase.isValidPassword(password) } returns false

        // When & Then
        assertThrows(InvalidPasswordException::class.java) {
            runBlocking {
                addUserUseCase.addUser(username, password)
            }
        }

        coVerify(exactly = 0) { loginUseCase.isUserExist(any()) }
        coVerify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `addUser should throw UserExistException when user already exists`() = runBlocking {
        // Given
        val username = "existingUser"
        val password = "ValidPass123"

        every { validateUserDataUseCase.isValidUserName(username) } returns true
        every { validateUserDataUseCase.isValidPassword(password) } returns true
        coEvery { loginUseCase.isUserExist(username) } returns true

        // When & Then
        assertThrows(UserExistException::class.java) {
            runBlocking {
                addUserUseCase.addUser(username, password)
            }
        }

        coVerify(exactly = 0) { userRepository.addUser(any()) }
    }

}
