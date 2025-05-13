package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.UserAssignedToProjectDto
import data.dto.UserDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.UserDataSource
import org.example.data.source.local.UserCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.example.logic.entities.UserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserCSVDataSourceTest {

    private lateinit var userReaderWriter: IReaderWriter<UserDto>
    private lateinit var userAssignedToProjectDataSource: UserAssignedToProjectDataSource
    private lateinit var mateTaskAssignmentDataSource: MateTaskAssignmentDataSource
    private lateinit var dataSource: UserDataSource

    private val user1 = UserDto(id = "1", username = "Thoraya", hashedPassword = "lll", UserRole.MATE.name)
    private val user2 = UserDto(id = "2", username = "Yasmeen", hashedPassword = "lll", UserRole.MATE.name)

    @BeforeEach
    fun setup() {
        userReaderWriter = mockk()
        userAssignedToProjectDataSource = mockk()
        mateTaskAssignmentDataSource = mockk()
        dataSource = UserCSVDataSource(userReaderWriter, userAssignedToProjectDataSource, mateTaskAssignmentDataSource)
    }

    @Test
    fun `addUser should append user and return true`() = runTest {
        // Given
        coEvery { userReaderWriter.append(listOf(user1)) } returns true

        // When
        val result = dataSource.addUser(user1)

        // Then
        assertThat(result).isTrue()
        coVerify { userReaderWriter.append(listOf(user1)) }
    }

    @Test
    fun `getAllUsers should return list of users`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1, user2)

        // When
        val result = dataSource.getAllUsers()

        // Then
        assertThat(result).containsExactly(user1, user2)
    }

    @Test
    fun `getUsersByProjectId should return correct users`() = runTest {
        // Given
        coEvery { userAssignedToProjectDataSource.getUsersAssignedToProjectByProjectId("p1") } returns
                listOf(UserAssignedToProjectDto(username = "1", projectId = "p1"))
        coEvery { userReaderWriter.read() } returns listOf(user1, user2)

        // When
        val result = dataSource.getUsersByProjectId("p1")

        // Then
        assertThat(result).containsExactly(user1)
    }

    @Test
    fun `isUserExist should return true when user exists`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1)

        // When
        val result = dataSource.isUserExist("Thoraya")

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isUserExist should return false when user does not exist`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1)

        // When
        val result = dataSource.isUserExist("notFound")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getUserById should return correct user`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1, user2)

        // When
        val result = dataSource.getUserById("2")

        // Then
        assertThat(result).isEqualTo(user2)
    }

    @Test
    fun `getUserById should return null when not found`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1)

        // When
        val result = dataSource.getUserById("3")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `updateUser should overwrite with updated user`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1, user2)
        coEvery { userReaderWriter.overWrite(any()) } returns true
        val updatedUser = user2.copy(username = "Yasmeenby")

        // When
        val result = dataSource.updateUser(updatedUser)

        // Then
        assertThat(result).isTrue()
        coVerify {
            userReaderWriter.overWrite(
                match {
                    it.any { u -> u.id == "2" && u.username == "Yasmeenby" } &&
                            it.any { u -> u.id == "1" && u.username == "Thoraya" }
                }
            )
        }
    }

    @Test
    fun `deleteUser should overwrite with filtered list`() = runTest {
        // Given
        coEvery { userReaderWriter.read() } returns listOf(user1, user2)
        coEvery { userReaderWriter.overWrite(any()) } returns true

        // When
        val result = dataSource.deleteUser(user2)

        // Then
        assertThat(result).isTrue()
        coVerify {
            userReaderWriter.overWrite(
                match { it.size == 1 && it[0] == user1 }
            )
        }
    }

    @Test
    fun `deleteUserFromProject should call underlying dataSource and return result`() = runTest {
        // Given
        coEvery { userAssignedToProjectDataSource.deleteUserFromProject("1", "Thoraya") } returns true

        // When
        val result = dataSource.deleteUserFromProject("1", "Thoraya")

        // Then
        assertThat(result).isTrue()
        coVerify { userAssignedToProjectDataSource.deleteUserFromProject("1", "Thoraya") }
    }

    @Test
    fun `addUserToProject should call underlying dataSource and return result`() = runTest {
        // Given
        coEvery { userAssignedToProjectDataSource.addUserToProject("1", "Thoraya") } returns true

        // When
        val result = dataSource.addUserToProject("1", "Thoraya")

        // Then
        assertThat(result).isTrue()
        coVerify { userAssignedToProjectDataSource.addUserToProject("1", "Thoraya") }
    }

    @Test
    fun `addUserToTask should call underlying dataSource and return result`() = runTest {
        // Given
        coEvery { mateTaskAssignmentDataSource.addUserToTask("Thoraya", "t1") } returns true

        // When
        val result = dataSource.addUserToTask("Thoraya", "t1")

        // Then
        assertThat(result).isTrue()
        coVerify { mateTaskAssignmentDataSource.addUserToTask("Thoraya", "t1") }
    }

    @Test
    fun `deleteUserFromTask should call underlying dataSource and return result`() = runTest {
        // Given
        coEvery { mateTaskAssignmentDataSource.deleteUserFromTask("Thoraya", "t1") } returns true

        // When
        val result = dataSource.deleteUserFromTask("Thoraya", "t1")

        // Then
        assertThat(result).isTrue()
        coVerify { mateTaskAssignmentDataSource.deleteUserFromTask("Thoraya", "t1") }
    }
}