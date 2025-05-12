package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import org.example.logic.ReadDataException
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import java.util.*
import kotlin.test.Test

class ManageUsersAssignedToProjectUseCaseTest {
    private lateinit var getProjectUseCase: GetProjectsUseCase
    private lateinit var userRepository: UserRepository
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase
    private val testUser = User(username = "user1", hashedPassword = "user1@test.com")
    private val anotherUser = User(username = "user2", hashedPassword = "user2@test.com")

    val projectId = UUID.randomUUID()
    val projectName = "Plan-Mate"

    @BeforeEach
    fun setUp() {
        getProjectUseCase = mockk()
        userRepository = mockk()
        loginUseCase = mockk()
        manageUsersAssignedToProjectUseCase =
            ManageUsersAssignedToProjectUseCase(userRepository, getProjectUseCase, loginUseCase)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUsersAssignedToProject() should return users when successful`() = runTest {
        // Given
        coEvery { userRepository.getUsersByProjectId(projectId) } returns listOf(testUser, anotherUser)

        // When
        val result = manageUsersAssignedToProjectUseCase.getUsersByProjectId(projectId)

        // Then
        assertThat(result).isEqualTo(listOf(testUser, anotherUser))
    }

    @Test
    fun `getUsersAssignedToProject() should filter out null users`() = runTest {
        // Given
        coEvery { userRepository.getUsersByProjectId(projectId) } returns listOf(testUser, anotherUser)

        // When
        val result = manageUsersAssignedToProjectUseCase.getUsersByProjectId(projectId)

        // Then
        assertThat(result).isEqualTo(listOf(testUser, anotherUser))

    }

    @Test
    fun `getUsersByProjectId() should propagate failure`() = runTest {
        //Given
        coEvery { userRepository.getUsersByProjectId(projectId) } throws UserDoesNotExistException()
        //When & Then
        assertThrows<UserDoesNotExistException> {
            manageUsersAssignedToProjectUseCase.getUsersByProjectId(projectId)
        }
    }

    @Test
    fun `addUserToProject() should return true when user exists and repo succeeds`() = runTest {
        //Given
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.addUserToProject(projectId, testUser.username) } returns true
        //When
        val result = manageUsersAssignedToProjectUseCase.addUserToProject(projectId, testUser.username)
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
        val result = manageUsersAssignedToProjectUseCase.addUserToProject(projectId, testUser.username)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addUserToProject() should throw when user does not exist`() = runTest {
        //Given
        coEvery { loginUseCase.isUserExist(testUser.username) } returns false
        //When & Then
        assertThrows<UserDoesNotExistException> {
            manageUsersAssignedToProjectUseCase.addUserToProject(projectId, testUser.username)
        }
        coVerify(exactly = 0) { userRepository.addUserToProject(any(), any()) }
    }


    @Test
    fun `deleteUserFromProject() should return true when project exists, user exists and repo succeeds`() = runTest {
        //Given
        val project =
            buildProject(id = projectId, name = projectName, stateId = UUID.randomUUID())
        coEvery { getProjectUseCase.getProjectByName(projectName) } returns project
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.deleteUserFromProject(projectId, testUser.username) } returns true
        //When
        val result = manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, testUser.username)
        //Then
        assertThat(result).isTrue()
        coVerify { userRepository.deleteUserFromProject(projectId, testUser.username) }
    }

    @Test
    fun `deleteUserFromProject() should return false when user exists and repo returns false`() = runTest {
        //Given
        val project =
            buildProject(id = projectId, name = projectName, stateId = UUID.randomUUID())
        coEvery { getProjectUseCase.getProjectByName(projectName) } returns project
        coEvery { loginUseCase.isUserExist(testUser.username) } returns true
        coEvery { userRepository.deleteUserFromProject(projectId, testUser.username) } returns false
        //When
        val result = manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, testUser.username)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteUserFromProject() should throw when user does not exist`() = runTest {
        //Given
        val project =
            org.example.logic.entities.Project(id = projectId, name = projectName, stateId = UUID.randomUUID())
        coEvery { getProjectUseCase.getProjectByName(projectName) } returns project
        coEvery { loginUseCase.isUserExist(testUser.username) } returns false
        //When & then
        assertThrows<UserDoesNotExistException> {
            manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, testUser.username)
        }
        coVerify(exactly = 0) { userRepository.deleteUserFromProject(any(), any()) }
    }

    @Test
    fun `deleteUserFromProject() should propagate getProjectByName failure`() = runTest {
        //Given
        coEvery { getProjectUseCase.getProjectByName(projectName) } throws RuntimeException("not found")
        //When & Then
        assertThrows<RuntimeException> {
            manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, testUser.username)
        }
    }
}