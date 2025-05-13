package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.MateTaskAssignmentDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.local.MateTaskAssignmentCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MateTaskAssignmentCSVDataSourceTest {

    private lateinit var readerWriter: IReaderWriter<MateTaskAssignmentDto>
    private lateinit var dataSource: MateTaskAssignmentDataSource

    private val dto1 = MateTaskAssignmentDto(userName = "Thoraya", taskId = "task1")
    private val dto2 = MateTaskAssignmentDto(userName = "Hanan", taskId = "task2")
    private val dto3 = MateTaskAssignmentDto(userName = "Thoraya", taskId = "task3")

    @BeforeEach
    fun setup() {
        readerWriter = mockk()
        dataSource = MateTaskAssignmentCSVDataSource(readerWriter)
    }

    @Test
    fun `addUserToTask should append assignment and return true`() = runTest {
        // Given
        val expectedDto = MateTaskAssignmentDto("Thoraya", "task1")
        coEvery { readerWriter.append(listOf(expectedDto)) } returns true

        // When
        val result = dataSource.addUserToTask("Thoraya", "task1")

        // Then
        assertThat(result).isTrue()
        coVerify { readerWriter.append(listOf(expectedDto)) }
    }

    @Test
    fun `addUserToTask should return false if append fails`() = runTest {
        // Given
        val expectedDto = MateTaskAssignmentDto("Thoraya", "task1")
        coEvery { readerWriter.append(listOf(expectedDto)) } returns false

        // When
        val result = dataSource.addUserToTask("Thoraya", "task1")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteUserFromTask should remove entries with matching taskId and return true`() = runTest {
        // Given
        coEvery { readerWriter.read() } returns listOf(dto1, dto2, dto3)
        coEvery { readerWriter.overWrite(any()) } returns true

        // When
        val result = dataSource.deleteUserFromTask("Thoraya", "task1")

        // Then
        assertThat(result).isTrue()
        coVerify {
            readerWriter.overWrite(
                match { it.none { dto -> dto.taskId == "task1" } }
            )
        }
    }

    @Test
    fun `deleteUserFromTask should return false if overwrite fails`() = runTest {
        // Given
        coEvery { readerWriter.read() } returns listOf(dto1, dto2)
        coEvery { readerWriter.overWrite(any()) } returns false

        // When
        val result = dataSource.deleteUserFromTask("Thoraya", "task1")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getUsersMateTaskByTaskId should return list of users assigned to taskId`() = runTest {
        // Given
        coEvery { readerWriter.read() } returns listOf(dto1, dto2, dto3)

        // When
        val result = dataSource.getUsersMateTaskByTaskId("task3")

        // Then
        assertThat(result).containsExactly(dto3)
    }

    @Test
    fun `getUsersMateTaskByTaskId should return empty list if no match`() = runTest {
        // Given
        coEvery { readerWriter.read() } returns listOf(dto1, dto2)

        // When
        val result = dataSource.getUsersMateTaskByTaskId("t99")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getUsersMateTaskByUserName should return list of tasks for the user`() = runTest {
        // Given
        coEvery { readerWriter.read() } returns listOf(dto1, dto2, dto3)

        // When
        val result = dataSource.getUsersMateTaskByUserName("Thoraya")

        // Then
        assertThat(result).containsExactly(dto1, dto3)
    }

    @Test
    fun `getUsersMateTaskByUserName should return empty list if user not found`() = runTest {
        // Given
        coEvery { readerWriter.read() } returns listOf(dto1, dto2)

        // When
        val result = dataSource.getUsersMateTaskByUserName("llll")

        // Then
        assertThat(result).isEmpty()
    }
}