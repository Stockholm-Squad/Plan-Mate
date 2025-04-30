package logic

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.AddUserUseCase
import org.example.utils.hashToMd5
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.security.NoSuchAlgorithmException

class AddUserUseCaseTest {

    private lateinit var addUserUseCase: AddUserUseCase
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        addUserUseCase = AddUserUseCase(userRepository)
    }

    @Test
    fun `should successfully add new user when username is unique`() {
        // Given
        val existingUsers = listOf(User("user1", "hash1"))
        val newUser = User("newUser", "password123")
        val expectedHashedUser = User("newUser", hashToMd5("password123"))

        every { userRepository.getAllUsers() } returns Result.success(existingUsers)
        every { userRepository.addUser(expectedHashedUser) } returns Result.success(true)

        // When
        val result = addUserUseCase.addUser(newUser)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
        verify(exactly = 1) { userRepository.getAllUsers() }
        verify(exactly = 1) { userRepository.addUser(expectedHashedUser) }
    }

    @Test
    fun `should fail with IllegalArgumentException when username exists`() {
        // Given
        val existingUsers = listOf(User("existingUser", "hash1"))
        val duplicateUser = User("existingUser", "password123")

        every { userRepository.getAllUsers() } returns Result.success(existingUsers)

        // When
        val result = addUserUseCase.addUser(duplicateUser)

        // Then
        assertThat(result.isFailure).isTrue()
assertThrows<IllegalArgumentException> { result.getOrThrow() }
        verify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `should fail when repository fails to get users`() {
        // Given
        val newUser = User("newUser", "password123")
        val expectedError = RuntimeException("Database error")

        every { userRepository.getAllUsers() } returns Result.failure(expectedError)

        // When
        val result = addUserUseCase.addUser(newUser)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
        verify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `should properly hash password`() {
        // Given
        val existingUsers = emptyList<User>()
        val newUser = User("testUser", "password123")
        val expectedHash = hashToMd5("password123")

        every { userRepository.getAllUsers() } returns Result.success(existingUsers)
        every { userRepository.addUser(User("testUser", expectedHash)) } returns Result.success(true)

        // When
        val result = addUserUseCase.addUser(newUser)

        // Then
        assertThat(result.isSuccess).isTrue()
        verify(exactly = 1) {
            userRepository.addUser(User("testUser", expectedHash))
        }
    }

    @Test
    fun `should fail when hashing fails`() {
        // Given
        val existingUsers = emptyList<User>()
        val newUser = User("testUser", "password123")

        every { userRepository.getAllUsers() } returns Result.success(existingUsers)
        every { userRepository.addUser(any()) } throws NoSuchAlgorithmException("MD5 not available")

        // When
        val result = addUserUseCase.addUser(newUser)

        // Then
        assertThat(result.isFailure).isTrue()

    }

}