package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.example.logic.entities.User
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.example.data.repo.UserRepositoryImp
import org.example.data.source.CurrentUserDataSource
import org.example.data.source.UserDataSource
import org.example.logic.*
import org.example.logic.utils.HashingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class UserRepositoryImpTest {

    private lateinit var userDataSource: UserDataSource
    private lateinit var userRepo: UserRepositoryImp
    private lateinit var currentUserDataSource: CurrentUserDataSource
    private lateinit var hashingService: HashingService

    @BeforeEach
    fun setUp() {
        userDataSource = mockk(relaxed = true)
        currentUserDataSource = mockk(relaxed = true)
        hashingService = mockk(relaxed = true)
        userRepo = UserRepositoryImp(
            userDataSource = userDataSource,
            currentUserDataSource = currentUserDataSource,
            hashingService = hashingService
        )
    }

    @Test
    fun `addUser() should return true when user added successfully`() = runTest {
        // Given
        coEvery { userDataSource.addUser(userDto) } returns true
        // When
        val result = userRepo.addUser(user)
        // Then
        assertThat(result).isTrue()

    }

    @Test
    fun `addUser() should throw UserNotAddedException when data source fails`() = runTest {
        // Given
        coEvery { userDataSource.addUser(userDto) } throws Exception("Database error")
        // When& Then
        assertThrows<UserNotAddedException> {
            userRepo.addUser(user)
        }
    }

    @Test
    fun `addUser() should return false when user adding fails`() = runTest {
        // Given
        coEvery { userDataSource.addUser(userDto) } returns false
        // When
        val result = userRepo.addUser(user)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getAllUsers() should return all users when called`() = runTest {
        // Given
        coEvery { userDataSource.getAllUsers() } returns userDtoList
        // When
        val result = userRepo.getAllUsers()
        assertThat(result).isEqualTo(usersList)

    }

    @Test
    fun `getAllUsers() should return exception when datasource fails`() = runTest {

        // Given
        coEvery { userDataSource.getAllUsers() } throws Exception("Database error")

        //When&Then
        assertThrows<UsersDoesNotExistException> {
            userRepo.getAllUsers()
        }

    }

    @Test
    fun `getUsersByProjectId() should return list of users if user exists `() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery { userDataSource.getUsersByProjectId(projectId.toString()) } returns userDtoList
        // When
        val result = userRepo.getUsersByProjectId(projectId)
        //Then
        assertThat(result).isEqualTo(usersList)
    }

    @Test
    fun `getUsersByProjectId() should return empty list of users if there is not any user assigned for that project`() =
        runTest {
            // Given
            val projectId = UUID.randomUUID()
            val emptyList = emptyList<User>()
            coEvery { userDataSource.getUsersByProjectId(projectId.toString()) } returns emptyList()
            // When
            val result = userRepo.getUsersByProjectId(projectId)
            //Then
            assertThat(result).isEqualTo(emptyList)
        }

    @Test
    fun `getUsersByProjectId() should throw exception when datasource fails `() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery { userDataSource.getUsersByProjectId(projectId.toString()) } throws Exception()
        //When&&Then
        assertThrows<UsersDoesNotExistException> {
            userRepo.getUsersByProjectId(projectId)
        }
    }

    @Test
    fun `addUserToProject() should return true when user added`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery {
            userDataSource.addUserToProject(
                projectId = projectId.toString(),
                username = "username"
            )
        } returns true
        // When
        val result = userRepo.addUserToProject(projectId = projectId, username = "username")
        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addUserToProject() should return false when user not added`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery {
            userDataSource.addUserToProject(
                projectId.toString(),
                username = "username"
            )
        } returns false
        // When
        val result = userRepo.addUserToProject(projectId = projectId, username = "username")
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addUserToProject() should throw exception when datasource fails`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery {
            userDataSource.addUserToProject(
                projectId.toString(),
                username = "username"
            )
        } throws Exception()
        //When&&Then
        assertThrows<UserNotAddedToProjectException> {
            userRepo.addUserToProject(projectId = projectId, username = "username")
        }
    }

    @Test
    fun `deleteUserFromProject() should return true when user deleted`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery {
            userDataSource.deleteUserFromProject(
                projectId.toString(),
                username = "username"
            )
        } returns true
        // When
        val result = userRepo.deleteUserFromProject(projectId = projectId, username = "username")
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `deleteUserFromProject() should return false when user not deleted`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery {
            userDataSource.deleteUserFromProject(
                projectId.toString(),
                username = "username"
            )
        } returns false
        // When
        val result = userRepo.deleteUserFromProject(projectId = projectId, username = "username")
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteUserFromProject() should throw exception when datasource fails`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        coEvery {
            userDataSource.deleteUserFromProject(
                projectId.toString(),
                username = "username"
            )
        } throws Exception()
        // When & Then
        assertThrows<UserNotDeletedFromProjectException> {
            userRepo.deleteUserFromProject(projectId = projectId, username = "username")
        }
    }

    @Test
    fun `addUserToTask() should return true when user added to task`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        coEvery {
            userDataSource.addUserToTask(
                username = "mateName",
                taskId = taskId.toString()
            )
        } returns true
        // When
        val result = userRepo.addUserToTask(username = "mateName", taskId = taskId)
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addUserToTask() should return false when user not added to task`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        coEvery {
            userDataSource.addUserToTask(
                username = "mateName",
                taskId = taskId.toString()
            )
        } returns false
        // When
        val result = userRepo.addUserToTask(username = "mateName", taskId = taskId)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addUserToTask() should throw exception when datasource fails`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        coEvery {
            userDataSource.addUserToTask(
                username = "mateName",
                taskId = taskId.toString()
            )
        } throws Exception()
        // When & Then
        assertThrows<UserNotAddedToTaskException> {
            userRepo.addUserToTask(username = "mateName", taskId = taskId)
        }
    }

    @Test
    fun `deleteUserFromTask() should return true when user is deleted from task`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        coEvery {
            userDataSource.deleteUserFromTask(
                username = "mateName",
                taskId = taskId.toString()
            )
        } returns true
        // When
        val result = userRepo.deleteUserFromTask(username = "mateName", taskId = taskId)
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `deleteUserFromTask() should return false when user is not deleted from task`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        coEvery {
            userDataSource.deleteUserFromTask(
                username = "mateName",
                taskId = taskId.toString()
            )
        } returns false
        // When
        val result = userRepo.deleteUserFromTask(username = "mateName", taskId = taskId)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteUserFromTask() should throw exception when datasource fails`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        coEvery {
            userDataSource.deleteUserFromTask(
                username = "mateName",
                taskId = taskId.toString()
            )
        } throws Exception()
        // When & Then
        assertThrows<UserNotDeletedFromTaskException> {
            userRepo.deleteUserFromTask(username = "mateName", taskId = taskId)
        }
    }
}