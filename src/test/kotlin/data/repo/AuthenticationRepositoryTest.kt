package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Role
import logic.model.entities.User
import org.example.logic.repository.AuthenticationRepository
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test

class AuthenticationRepositoryTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private val testHashedPassword = "hashed_$12222"

    @BeforeTest
    fun setup(){
         authenticationRepository = mockk(relaxed = true)

    }

    @Test
    fun `getUserByUserName() should return correct result when correct userName`(){
        //Given
        val username="Halla"
      val expectedUser = User(username = username, hashedPassword = testHashedPassword, role = Role.MATE)
        every { authenticationRepository.getUserByUserName(username) } returns Result.success(expectedUser)
        //When
        val result = authenticationRepository.getUserByUserName(username)
        //Then
        assertThat(result).isEqualTo(Result.success(expectedUser))
        verify(exactly = 1) { authenticationRepository.getUserByUserName(username) }
    }
    @Test
    fun `getUserByUserName() should return result failer when is empty username ` (){
        //Given
        val username=""
        val exception=NoSuchElementException("user not found")
        every { authenticationRepository.getUserByUserName(username) } returns Result.failure(exception)
        //When
        val result = authenticationRepository.getUserByUserName(username)
        //Then
        assertThrows<NoSuchElementException> { result.getOrThrow()}
        verify(exactly = 1) { authenticationRepository.getUserByUserName(username) }

    }

    @Test
    fun `getAllUsers() should return list of users  when is there users data`(){
        //Given
        val userList = listOf(
            User(username = "mate1", hashedPassword = testHashedPassword, role = Role.MATE),
            User(username = "mate2", hashedPassword = testHashedPassword, role = Role.MATE),
            User(username = "admin", hashedPassword = testHashedPassword, role = Role.ADMIN)
        )
        every { authenticationRepository.getAllUsers() } returns Result.success(userList)
        //When
        val result = authenticationRepository.getAllUsers()
        //Then
        assertThat(result).isEqualTo(Result.success(userList))
        verify(exactly = 1) { authenticationRepository.getAllUsers() }

    }

    @Test
    fun `getAllUsers() should return  exception message when is no users ` (){
        //Given
        val exception=NoSuchElementException("no users found")
        every { authenticationRepository.getAllUsers() } returns Result.failure(exception)
        //When
        val result = authenticationRepository.getAllUsers()
        //Then
        assertThrows<NoSuchElementException> { result.getOrThrow()}
        verify(exactly = 1) { authenticationRepository.getAllUsers() }

    }

    @Test
    fun `addUser should return success when user is valid`() {
        // Given
        val newUser = User(
            username = "newUser",
            hashedPassword = testHashedPassword,
            role = Role.MATE
        )
        every { authenticationRepository.addUser(newUser) } returns Result.success(true)

        // When
        val result = authenticationRepository.addUser(newUser)

        // Then
        assertThat(result).isEqualTo(Result.success(true))
        verify(exactly = 1) { authenticationRepository.addUser(newUser) }

    }

    @Test
    fun `addUser should fail when username is empty`() {
        // Given
        val invalidUser = User(
            username = "",
            hashedPassword = testHashedPassword,
            role = Role.MATE
        )
        every { authenticationRepository.addUser(invalidUser) } returns Result.failure(
            IllegalArgumentException("Username cannot be empty")
        )

        // When
        val result = authenticationRepository.addUser(invalidUser)

        // Then
       assertThrows<IllegalArgumentException>(){ result.getOrThrow()}
        verify(exactly = 1) { authenticationRepository.addUser(invalidUser) }

    }

    @Test
    fun `addUser should fail when password is empty`() {
        // Given
        val invalidUser = User(
            username = "validUser",
            hashedPassword = "",
            role = Role.MATE
        )
        every { authenticationRepository.addUser(invalidUser) } returns Result.failure(
            IllegalArgumentException("Password cannot be empty")
        )

        // When
        val result = authenticationRepository.addUser(invalidUser)

        // Then
      assertThrows<IllegalArgumentException>(){ result.getOrThrow()}
        verify(exactly = 1) { authenticationRepository.addUser(invalidUser) }

    }

}