package data.repo

import com.google.common.truth.Truth.assertThat
import data.models.UserAssignedToProjectModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.models.entities.User
import logic.models.entities.UserRole
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.UsersDataAreEmpty
import logic.models.exceptions.WriteDataException
import org.example.data.datasources.mate_task_assignment_data_source.MateTaskAssignmentCsvDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.UserAssignedToProjectCsvDataSource
import org.example.data.datasources.user_data_source.UserCsvDataSource
import org.example.data.models.UserModel
import org.example.data.repo.UserRepositoryImp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.util.*

class UserRepositoryImpTest {

    private lateinit var userRepository: UserRepositoryImp
    private lateinit var userCsvDataSource: UserCsvDataSource
    private lateinit var userAssignedToProjectCsvDataSource: UserAssignedToProjectCsvDataSource
    private lateinit var mateTaskAssignmentCsvDataSource: MateTaskAssignmentCsvDataSource

    private val projectUUID1 = UUID.randomUUID()
    private val projectUUID2 = UUID.randomUUID()
    private val testUserAssigned = UserAssignedToProjectModel(projectId = projectUUID1.toString(), userName = "user1")
    private val userAssignedWithDifferentUserName =
        UserAssignedToProjectModel(projectId = projectUUID1.toString(), userName = "user2")
    private val userAssignedWithDifferentProjectId =
        UserAssignedToProjectModel(projectId = projectUUID2.toString(), userName = "user1")

    private val testUserUUID1 = UUID.randomUUID()
    private val testUserEntity1 = User(
        id = testUserUUID1,
        username = "testUser",
        hashedPassword = "hashedPassword",
        userRole = UserRole.MATE
    )
    private val testUserModel1 = UserModel(
        id = testUserUUID1.toString(),
        username = "testUser",
        hashedPassword = "hashedPassword",
        role = UserRole.MATE.toString()
    )

    private val testUserUUID2 = UUID.randomUUID()
    private val testUserEntity2 = User(
        id = testUserUUID2,
        username = "testUser",
        hashedPassword = "hashedPassword",
        userRole = UserRole.MATE
    )
    private val testUserModel2 = UserModel(
        id = testUserUUID2.toString(),
        username = "testUser",
        hashedPassword = "hashedPassword",
        role = UserRole.MATE.toString()
    )

    @BeforeEach
    fun setUp() {
        userCsvDataSource = mockk(relaxed = true)
        userAssignedToProjectCsvDataSource = mockk(relaxed = true)
        mateTaskAssignmentCsvDataSource = mockk(relaxed = true)
        userRepository =
            UserRepositoryImp(userCsvDataSource, userAssignedToProjectCsvDataSource, mateTaskAssignmentCsvDataSource)
    }

    @Test
    fun `addUser should return success when datasource writes successfully`() {
        // Given
        every { userCsvDataSource.append(listOf(testUserModel1)) } returns Result.success(true)

        // When
        val result = userRepository.addUser(testUserEntity1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        verify { userCsvDataSource.append(listOf(testUserModel1)) }
    }

    @Test
    fun `addUser should return failure when datasource write fails`() {
        // Given
        val expectedException = IOException("Write failed")
        every { userCsvDataSource.append(listOf(testUserModel1)) } returns Result.failure(expectedException)

        // When
        val result = userRepository.addUser(testUserEntity1)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        verify { userCsvDataSource.append(listOf(testUserModel1)) }
    }

    @Test
    fun `getAllUsers should return success with users when datasource reads successfully`() {
        // Given
        val expectedUsers = listOf(testUserModel1, testUserModel2)
        every { userCsvDataSource.read() } returns Result.success(expectedUsers)

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
        verify { userCsvDataSource.read() }
    }

    @Test
    fun `getAllUsers should return failure when datasource read fails`() {
        // Given
        val expectedException = UsersDataAreEmpty()
        every { userCsvDataSource.read() } returns Result.failure(expectedException)

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        verify { userCsvDataSource.read() }
    }

    @Test
    fun `getAllUsers should return empty list when datasource returns empty list`() {
        // Given
        every { userCsvDataSource.read() } returns Result.success(emptyList())

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() ?: false)
        verify { userCsvDataSource.read() }
    }

    @Test
    fun `addUser should propagate false when datasource returns false`() {
        // Given
        every { userCsvDataSource.append(listOf(testUserModel1)) } returns Result.success(false)

        // When
        val result = userRepository.addUser(testUserEntity1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        verify { userCsvDataSource.append(listOf(testUserModel1)) }
    }

    @Nested
    inner class UsersAssignedToProjectTests {
        @Test
        fun `getUsersAssignedToProject should return list of usernames for given project`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.success(
                listOf(testUserAssigned, userAssignedWithDifferentUserName)
            )

            val result = userRepository.getUsersByProjectId(projectUUID1)

            assertThat(result.getOrNull()).containsExactly("user1", "user2")
        }

        @Test
        fun `getUsersAssignedToProject should return empty list when no users for project`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.success(
                listOf(UserAssignedToProjectModel(projectId = "2", userName = "user3"))
            )

            val result = userRepository.getUsersByProjectId(projectUUID1)

            assertThat(result.getOrNull()).isEmpty()
        }

        @Test
        fun `getUsersAssignedToProject should return failure when read fails`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.failure(
                ReadDataException()
            )

            val result = userRepository.getUsersByProjectId(projectUUID1)

            assertThrows<ReadDataException> { result.getOrThrow() }
        }

        @Test
        fun `addUserAssignedToProject should append user and return success`() {
            every { userAssignedToProjectCsvDataSource.append(any()) } returns Result.success(true)

            val result = userRepository.addUserToProject(projectUUID1, "user1")

            assertThat(result.isSuccess).isTrue()
            verify {
                userAssignedToProjectCsvDataSource.append(
                    listOf(UserAssignedToProjectModel(projectId = "1", userName = "user1"))
                )
            }
        }

        @Test
        fun `addUserAssignedToProject should return failure when write fails`() {
            every { userAssignedToProjectCsvDataSource.append(any()) } returns Result.failure(
                WriteDataException()
            )

            val result = userRepository.addUserToProject(projectUUID1, "user1")

            assertThrows<WriteDataException> { result.getOrThrow() }
        }

        @Test
        fun `deleteUserAssignedToProject should remove user and return success`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.success(
                listOf(testUserAssigned, userAssignedWithDifferentUserName)
            )
            every { userAssignedToProjectCsvDataSource.overWrite(any()) } returns Result.success(true)

            val result = userRepository.deleteUserFromProject(projectUUID1, "user1")

            assertThat(result.isSuccess).isTrue()
            verify { userAssignedToProjectCsvDataSource.overWrite(listOf(userAssignedWithDifferentUserName)) }
        }

        @Test
        fun `deleteUserAssignedToProject should return success when user not found`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.success(
                listOf(userAssignedWithDifferentUserName)
            )
            every { userAssignedToProjectCsvDataSource.overWrite(any()) } returns Result.success(true)

            val result = userRepository.deleteUserFromProject(projectUUID1, "user1")

            assertThat(result.isSuccess).isTrue()
            verify { userAssignedToProjectCsvDataSource.overWrite(listOf(userAssignedWithDifferentUserName)) }
        }

        @Test
        fun `deleteUserAssignedToProject should return success when project not found`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.success(
                listOf(userAssignedWithDifferentProjectId)
            )
            every { userAssignedToProjectCsvDataSource.overWrite(any()) } returns Result.success(true)

            val result = userRepository.deleteUserFromProject(projectUUID1, "user1")

            assertThat(result.isSuccess).isTrue()
            verify { userAssignedToProjectCsvDataSource.overWrite(listOf(userAssignedWithDifferentProjectId)) }
        }

        @Test
        fun `deleteUserAssignedToProject should return failure when read fails`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.failure(
                ReadDataException()
            )

            val result = userRepository.deleteUserFromProject(projectUUID1, "user1")

            assertThrows<ReadDataException> { result.getOrThrow() }
        }

        @Test
        fun `deleteUserAssignedToProject should return failure when write fails`() {
            every { userAssignedToProjectCsvDataSource.read() } returns Result.success(
                listOf(testUserAssigned, userAssignedWithDifferentUserName)
            )
            every { userAssignedToProjectCsvDataSource.overWrite(any()) } returns Result.failure(
                WriteDataException()
            )

            val result = userRepository.deleteUserFromProject(projectUUID1, "user1")

            assertThrows<WriteDataException> { result.getOrThrow() }
        }
    }


}