package logic

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Role
import logic.model.entities.User
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.authentication.AddUserUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows

class AddUserUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var addUserUseCase: AddUserUseCase

    @BeforeEach
    fun setUp() {
        authRepository = mockk()
        addUserUseCase = AddUserUseCase(authRepository)
    }

    @Test
    fun `should append new user when data is valid`() {
        // given
        val existingUsers = listOf(
            User("user1", "hash1", Role.MATE),
            User("user2", "hash2", Role.ADMIN)
        )
        val newUser = User("newUser", "newHash", Role.MATE)

        every { authRepository.getAllUsers() } returns Result.success(existingUsers)
        every { authRepository.addUser(newUser) } returns Result.success(true)

        // when
        val result = addUserUseCase.addUser(newUser)

        // then
        assertThat(result.isSuccess).isTrue()
        verify (exactly = 1) { addUserUseCase.addUser(newUser) }
    }
    @Test
    fun `should reject weak passwords`() {
        // given
        val weakPasswordUser = User(
            username = "validUser",
            hashedPassword = "weak", // Too short
            role = Role.MATE
        )
         every { addUserUseCase.addUser(weakPasswordUser) } returns Result.failure(IllegalArgumentException())
        // when
        val result = addUserUseCase.addUser(weakPasswordUser)

        // then
       assertThrows<IllegalArgumentException>(){result.getOrThrow()}
        verify(exactly = 1) { addUserUseCase.addUser(weakPasswordUser) }
    }

    @Test
    fun `should reject empty username`() {
        // given
        val emptyUsernameUser = User(
            username = "", // Empty username
            hashedPassword = "validPassword123!",
            role = Role.MATE
        )
        every { addUserUseCase.addUser(emptyUsernameUser) } returns Result.failure(IllegalArgumentException())

        // when
        val result = addUserUseCase.addUser(emptyUsernameUser)

        // then
        assertThrows<IllegalArgumentException>(){result.getOrThrow()}
        verify(exactly = 1) { addUserUseCase.addUser(emptyUsernameUser) }
    }

    @Test
    fun `should throw when user already exists`() {
        // given
        val existingUser = User("existing", "hash", Role.MATE)
        every { authRepository.getAllUsers() } returns Result.success(listOf(existingUser))

        // when
        val result = addUserUseCase.addUser(existingUser.copy(hashedPassword = "newHash"))

        // then
        assertThrows<IllegalArgumentException>(){result.getOrThrow()}
        verify (exactly = 1) { addUserUseCase.addUser(existingUser.copy(hashedPassword = "newHash")) }

    }


    @Test
    fun `should fail when cannot append user`() {
        // given
        val appendError = Exception("File append error")
        val user = User("newUser", "newHash", Role.MATE)
        every { authRepository.getAllUsers() } returns Result.success(emptyList())
        every { authRepository.addUser(any()) } returns Result.failure(appendError)

        // when
        val result = addUserUseCase.addUser(user)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(appendError)
        verify (exactly = 1) { addUserUseCase.addUser(user) }
    }
}