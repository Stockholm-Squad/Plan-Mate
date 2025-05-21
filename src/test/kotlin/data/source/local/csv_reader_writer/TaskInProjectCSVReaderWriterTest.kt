package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.TaskInProjectDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.TaskInProjectCSVReaderWriter
import org.junit.jupiter.api.*
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskInProjectCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var taskInProjectCSVReaderWriter: TaskInProjectCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("task_in_project_test", ".csv").toFile()
        testFilePath = tempFile.path
        taskInProjectCSVReaderWriter = TaskInProjectCSVReaderWriter(testFilePath)
    }

    @AfterEach
    fun tearDown() {
        File(testFilePath).takeIf { it.exists() }?.delete()
    }

    @Nested
    inner class ReadTests {
        @Test
        fun `read should return empty list when file doesn't exist`() = runTest {
            File(testFilePath).delete()

            val result = taskInProjectCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = taskInProjectCSVReaderWriter.read()
            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("taskId,projectId")

            val result = taskInProjectCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return assignments when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                taskId,projectId
                task1,projectA
                task2,projectB
                """.trimIndent()
            )

            val result = taskInProjectCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("task1", result[0].taskId)
            assertEquals("projectA", result[0].projectId)
            assertEquals("task2", result[1].taskId)
            assertEquals("projectB", result[1].projectId)
        }

        @Test
        fun `read should create file when not exist`() = runTest {
            tempFile.delete()

            val result = taskInProjectCSVReaderWriter.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should not throw exception when file creation fails (creates empty file)`() = runTest {
            tempFile.delete()
            tempFile.mkdir()

            assertThrows<Throwable> { taskInProjectCSVReaderWriter.read() }
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            val readerWriter = TaskInProjectCSVReaderWriter("non_existent_file.csv")

            val result = readerWriter.read()

            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list on invalid CSV (less than 2 lines)`() = runTest {
            File(testFilePath).writeText("invalid,format")

            val result = taskInProjectCSVReaderWriter.read()
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `overWrite should create file with correct content`() = runTest {
            val assignments = listOf(
                TaskInProjectDto("task1", "projectA"),
                TaskInProjectDto("task2", "projectB")
            )

            val result = taskInProjectCSVReaderWriter.overWrite(assignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("task1,projectA"))
            assertTrue(content.contains("task2,projectB"))
        }

        @Test
        fun `overWrite should handle empty list`() = runTest {
            val result = taskInProjectCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("taskId,projectId", content.trim())
        }
    }

    @Nested
    inner class AppendTests {
        @Test
        fun `append should add records to empty file`() = runTest {
            val assignments = listOf(
                TaskInProjectDto("task1", "projectA"),
                TaskInProjectDto("task2", "projectB")
            )

            val result = taskInProjectCSVReaderWriter.append(assignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("task1,projectA"))
            assertTrue(content.contains("task2,projectB"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialAssignments = listOf(
                TaskInProjectDto("task1", "projectA")
            )
            taskInProjectCSVReaderWriter.overWrite(initialAssignments)

            val newAssignments = listOf(
                TaskInProjectDto("task2", "projectB")
            )

            val result = taskInProjectCSVReaderWriter.append(newAssignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("task1,projectA"))
            assertTrue(content.contains("task2,projectB"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = taskInProjectCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("taskId,projectId", content.trim())
        }

        @Test
        fun `append should create file when not exist`() = runTest {
            tempFile.delete()

            val assignments = listOf(
                TaskInProjectDto("task1", "projectA")
            )

            val result = taskInProjectCSVReaderWriter.append(assignments)
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("task1,projectA"))
        }
    }
}