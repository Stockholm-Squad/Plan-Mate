package data.repo

import com.google.common.truth.Truth.assertThat
import data.dto.UserAssignedToProjectDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.WriteDataException
import org.example.data.source.local.MateTaskAssignmentCSVReaderWriter
import org.example.data.source.local.UserAssignedToProjectCSVReaderWriter
import org.example.data.source.local.UserCSVReaderWriter
import data.dto.UserDto
import org.example.data.repo.UserRepositoryImp
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.model.exceptions.UsersDataAreEmptyException
import org.example.logic.model.exceptions.WriteDataException
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
    private lateinit var userCsvDataSource: UserCSVReaderWriter
    private lateinit var userAssignedToProjectCsvDataSource: UserAssignedToProjectCSVReaderWriter
    private lateinit var mateTaskAssignmentCSVReaderWriter: MateTaskAssignmentCSVReaderWriter

    private val projectUUID1 = UUID.randomUUID()
    private val projectUUID2 = UUID.randomUUID()
    private val testUserAssigned = UserAssignedToProjectDto(projectId = projectUUID1.toString(), userName = "user1")
    private val userAssignedWithDifferentUserName =
        UserAssignedToProjectDto(projectId = projectUUID1.toString(), userName = "user2")
    private val userAssignedWithDifferentProjectId =
        UserAssignedToProjectDto(projectId = projectUUID2.toString(), userName = "user1")

    private val testUserUUID1 = UUID.randomUUID()
    private val testUserEntity1 = User(
        id = testUserUUID1,
        username = "testUser",
        hashedPassword = "hashedPassword",
        userRole = UserRole.MATE
    )
    private val testUserDto1 = UserDto(
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
    private val testUserDto2 = UserDto(
        id = testUserUUID2.toString(),
        username = "testUser",
        hashedPassword = "hashedPassword",
        role = UserRole.MATE.toString()
    )

    @BeforeEach
    fun setUp() {
        userCsvDataSource = mockk(relaxed = true)
        userAssignedToProjectCsvDataSource = mockk(relaxed = true)
        mateTaskAssignmentCSVReaderWriter = mockk(relaxed = true)
        userRepository =
            UserRepositoryImp(userCsvDataSource, userAssignedToProjectCsvDataSource, mateTaskAssignmentCSVReaderWriter)
    }

    @Test
    fun `addUser should return success when datasource writes successfully`() {
        // Given
        every { userCsvDataSource.append(listOf(testUserDto1)) } returns Result.success(true)

        // When
        val result = userRepository.addUser(testUserEntity1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        verify { userCsvDataSource.append(listOf(testUserDto1)) }
    }

    @Test
    fun `addUser should return failure when datasource write fails`() {
        // Given
        val expectedException = IOException("Write failed")
        every { userCsvDataSource.append(listOf(testUserDto1)) } returns Result.failure(expectedException)

        // When
        val result = userRepository.addUser(testUserEntity1)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        verify { userCsvDataSource.append(listOf(testUserDto1)) }
    }

    @Test
    fun `getAllUsers should return success with users when datasource reads successfully`() {
        // Given
        val expectedUsers = listOf(testUserDto1, testUserDto2)
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
        val expectedException = UsersDataAreEmptyException()
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
        every { userCsvDataSource.append(listOf(testUserDto1)) } returns Result.success(false)

        // When
        val result = userRepository.addUser(testUserEntity1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        verify { userCsvDataSource.append(listOf(testUserDto1)) }
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
                listOf(UserAssignedToProjectDto(projectId = "2", userName = "user3"))
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
                    listOf(UserAssignedToProjectDto(projectId = "1", userName = "user1"))
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