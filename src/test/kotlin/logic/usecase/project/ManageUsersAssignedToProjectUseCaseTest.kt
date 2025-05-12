package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import org.example.logic.ReadDataException
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class ManageUsersAssignedToProjectUseCaseTest {
    private lateinit var getProjectUseCase: GetProjectsUseCase
    private lateinit var userRepository: UserRepository
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var useCase: ManageUsersAssignedToProjectUseCase
    private val testUser = User(username = "user1", hashedPassword = "user1@test.com")
    private val anotherUser = User(username = "user2", hashedPassword = "user2@test.com")

    val id = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
       getProjectUseCase = mockk()
        userRepository = mockk()
        loginUseCase=mockk()
        useCase = ManageUsersAssignedToProjectUseCase(userRepository,getProjectUseCase, loginUseCase )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUsersAssignedToProject() should return users when successful`() = runTest {
        // Given
        coEvery{ userRepository.getAllUsers() } returns listOf(testUser, anotherUser)

        // When
        val result = useCase.getUsersByProjectId(id)

        // Then
        assertThat{result}.isEqualTo(listOf(testUser, anotherUser))
    }

    @Test
    fun `getUsersAssignedToProject() should filter out null users`() = runTest {
        // Given
        coEvery{ userRepository.getAllUsers() } returns listOf(testUser, anotherUser)

        // When
        val result = useCase.getUsersByProjectId(id)

        // Then
        assertThat{result}.isEqualTo(listOf(testUser, anotherUser))

    }

    @Test
    fun `getUsersAssignedToProject() should propagate project repository failure`() = runTest {
        // Given
        coEvery{ userRepository.getAllUsers() } returns listOf(
                User(
                    username = "user1",
                    hashedPassword = "",
                    userRole = UserRole.MATE
                ))

        // When & Then

        assertThrows<ReadDataException> {useCase.getUsersByProjectId(id)
        }
    }

    @Test
    fun `addUserAssignedToProject() should return success when repository succeeds`() = runTest {
        // Given

        // When
        val result = useCase.addUserToProject(UUID.randomUUID(), "user1")

        // Then

    }

    @Test
    fun `addUserAssignedToProject should propagate failure`() = runTest {
        // Given
//        coEvery{ projectRepository.addUserAssignedToProject("1", "user1") } returns Result.failure(expectedException)

        // When
        val result = useCase.addUserToProject(UUID.randomUUID(), "user1")

        // Then
        assertThrows<Throwable> { result }
    }

    @Test
    fun `deleteUserAssignedToProject should return true when user exists and deletion succeeds`() = runTest {
        // Given
//        coEvery{ projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user1", "user2"))
//        coEvery{ projectRepository.deleteUserAssignedToProject("1", "user1") } returns Result.success(true)

        // When
        val result = useCase.deleteUserFromProject("id", "user1")

        // Then
    }

    @Test
    fun `deleteUserAssignedToProject should return false when user doesn't exist`() = runTest {
        // Given
//        coEvery{ projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user2"))

        // When
        val result = useCase.deleteUserFromProject("id", "user1")

        // Then

    }

    @Test
    fun `deleteUserAssignedToProject should propagate read failure`() = runTest {
        // Given
        val expectedException = ReadDataException()
//        coEvery{ projectRepository.getUsersAssignedToProject("1") } returns Result.failure(expectedException)

        // When
        val result = useCase.deleteUserFromProject("id", "user1")

        // Then
        assertThrows<ReadDataException> { result}
    }

    @Test
    fun `deleteUserAssignedToProject should propagate delete failure`() = runTest {
        // Given
//        coEvery{ projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user1"))
//        coEvery{ projectRepository.deleteUserAssignedToProject("1", "user1") } returns Result.failure(expectedException)

        // When
        val result = useCase.deleteUserFromProject("id", "user1")

        // Then
        assertThrows<Throwable> { result}
    }
}