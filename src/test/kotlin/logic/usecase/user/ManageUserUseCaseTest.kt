package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserDoesNotExistException
import org.example.logic.UserExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.user.ManageUserUseCase
import org.example.logic.utils.HashingService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildProject
import java.util.*

class ManageUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var validateUserDataUseCase: ValidateUserDataUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var hashingService: HashingService
    private lateinit var manageUserUseCase: ManageUserUseCase
    private lateinit var getProjectsUseCase: GetProjectsUseCase

    private val testUser = User(username = "user1", hashedPassword = "user1@test.com")
    private val anotherUser = User(username = "user2", hashedPassword = "user2@test.com")

    val projectId = UUID.randomUUID()
    val projectName = "Plan-Mate"

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        validateUserDataUseCase = mockk()
        loginUseCase = mockk()
        hashingService = mockk()
        getProjectsUseCase = mockk()
        manageUserUseCase = ManageUserUseCase(
            userRepository,
            validateUserDataUseCase,
            loginUseCase,
            hashingService,
            getProjectsUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `addUser should return true when user is valid and does not exist`() = runTest {
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
        val result = manageUserUseCase.addUser(username, password)

        // That
        assertThat(result).isTrue()
        coVerify {
            loginUseCase.isUserExist(username)
            userRepository.addUser(any())
        }
    }

    @Test
    fun `addUser should throw InvalidUserNameException when username is invalid`() = runTest {
        // Given
        val username = "!"
        val password = "Valid123"

        every { validateUserDataUseCase.isValidUserName(username) } returns false

        // When & Then
        assertThrows(InvalidUserNameException::class.java) {
            runBlocking {
                manageUserUseCase.addUser(username, password)
            }
        }

        coVerify(exactly = 0) { loginUseCase.isUserExist(any()) }
        coVerify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `addUser should throw InvalidPasswordException when password is invalid`() = runTest {
        // Given
        val username = "validUser"
        val password = "123"

        every { validateUserDataUseCase.isValidUserName(username) } returns true
        every { validateUserDataUseCase.isValidPassword(password) } returns false

        // When & Then
        assertThrows(InvalidPasswordException::class.java) {
            runBlocking {
                manageUserUseCase.addUser(username, password)
            }
        }

        coVerify(exactly = 0) { loginUseCase.isUserExist(any()) }
        coVerify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `addUser should throw UserExistException when user already exists`() = runTest {
        // Given
        val username = "existingUser"
        val password = "ValidPass123"

        every { validateUserDataUseCase.isValidUserName(username) } returns true
        every { validateUserDataUseCase.isValidPassword(password) } returns true
        coEvery { loginUseCase.isUserExist(username) } returns true

        // When & Then
        assertThrows(UserExistException::class.java) {
            runBlocking {
                manageUserUseCase.addUser(username, password)
            }
        }

        coVerify(exactly = 0) { userRepository.addUser(any()) }
    }

    @Test
    fun `addUser should return false when user is valid and userRepository returns false`() = runTest {
        // Given
        val username = "newUser"
        val password = "StrongPass123"
        val hashed = "hashed_pass"

        every { validateUserDataUseCase.isValidUserName(username) } returns true
        every { validateUserDataUseCase.isValidPassword(password) } returns true
        coEvery { loginUseCase.isUserExist(username) } returns false
        every { hashingService.hash(password) } returns hashed
        coEvery { userRepository.addUser(any()) } returns false

        // When
        val result = manageUserUseCase.addUser(username, password)

        // Then
        assertThat(result).isFalse()
        coVerify {
            loginUseCase.isUserExist(username)
            userRepository.addUser(any())
        }
    }

    @Test
    fun `getUsersAssignedToProject() should return users when successful`() = runTest {
        // Given
        coEvery { userRepository.getUsersByProjectId(projectId) } returns listOf(testUser, anotherUser)

        // When
        val result = manageUserUseCase.getUsersByProjectId(projectId)

        // Then
        assertThat(result).isEqualTo(listOf(testUser, anotherUser))
    }

    @Test
    fun `getUsersAssignedToProject() should filter out null users`() = runTest {
        // Given
        coEvery { userRepository.getUsersByProjectId(projectId) } returns listOf(testUser, anotherUser)

        // When
        val result = manageUserUseCase.getUsersByProjectId(projectId)

        // Then
        assertThat(result).isEqualTo(listOf(testUser, anotherUser))

    }

    @Test
    fun `getUsersByProjectId() should propagate failure`() = runTest {
        //Given
        coEvery { userRepository.getUsersByProjectId(projectId) } throws UserDoesNotExistException()
        //When & Then
        org.junit.jupiter.api.assertThrows<UserDoesNotExistException> {
            manageUserUseCase.getUsersByProjectId(projectId)
        }
    }

    @Test
    fun `addUserToProject() should return true when user exists and repo succeeds`() = runTest {
        //Given
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.addUserToProject(projectId, testUser.username) } returns true
        //When
        val result = manageUserUseCase.addUserToProject(projectId, testUser.username)
        //Then
        assertThat(result).isTrue()
        coVerify { userRepository.addUserToProject(projectId, testUser.username) }
    }

    @Test
    fun `addUserToProject() should return false when user exists and repo returns false`() = runTest {
        //Given
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.addUserToProject(projectId, testUser.username) } returns false
        //When
        val result = manageUserUseCase.addUserToProject(projectId, testUser.username)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addUserToProject() should throw when user does not exist`() = runTest {
        //Given
        coEvery { loginUseCase.isUserExist(testUser.username) } returns false
        //When & Then
        org.junit.jupiter.api.assertThrows<UserDoesNotExistException> {
            manageUserUseCase.addUserToProject(projectId, testUser.username)
        }
        coVerify(exactly = 0) { userRepository.addUserToProject(any(), any()) }
    }


    @Test
    fun `deleteUserFromProject() should return true when project exists, user exists and repo succeeds`() = runTest {
        //Given
        val project =
            buildProject(id = projectId, name = projectName, stateId = UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.deleteUserFromProject(projectId, testUser.username) } returns true
        //When
        val result = manageUserUseCase.deleteUserFromProject(projectName, testUser.username)
        //Then
        assertThat(result).isTrue()
        coVerify { userRepository.deleteUserFromProject(projectId, testUser.username) }
    }

    @Test
    fun `deleteUserFromProject() should return false when user exists and repo returns false`() = runTest {
        //Given
        val project =
            buildProject(id = projectId, name = projectName, stateId = UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.deleteUserFromProject(projectId, testUser.username) } returns false
        //When
        val result = manageUserUseCase.deleteUserFromProject(projectName, testUser.username)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteUserFromProject() should throw when user does not exist`() = runTest {
        //Given
        val project =
            org.example.logic.entities.Project(id = projectId, title = projectName, stateId = UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
        coEvery { loginUseCase.isUserExist(testUser.username) } returns false
        //When & then
        org.junit.jupiter.api.assertThrows<UserDoesNotExistException> {
            manageUserUseCase.deleteUserFromProject(projectName, testUser.username)
        }
        coVerify(exactly = 0) { userRepository.deleteUserFromProject(any(), any()) }
    }

    @Test
    fun `deleteUserFromProject() should propagate getProjectByName failure`() = runTest {
        //Given
        coEvery { getProjectsUseCase.getProjectByName(projectName) } throws RuntimeException("not found")
        //When & Then
        org.junit.jupiter.api.assertThrows<RuntimeException> {
            manageUserUseCase.deleteUserFromProject(projectName, testUser.username)
        }
    }
}
