package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.TaskDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.TaskCSVReaderWriter
import org.junit.jupiter.api.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var taskCSVReaderWriter: TaskCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("task_test", ".csv").toFile()
        testFilePath = tempFile.path
        taskCSVReaderWriter = TaskCSVReaderWriter(testFilePath)
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

            val result = taskCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = taskCSVReaderWriter.read()
            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("id,projectName,title,description,stateId,createdDate,updatedDate")

            val result = taskCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return tasks when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                id,projectName,title,description,stateId,createdDate,updatedDate
                task1,projectA,Title 1,Description 1,state1,2023-01-01,2023-01-02
                task2,projectB,Title 2,Description 2,state2,2023-02-01,2023-02-02
                """.trimIndent()
            )

            val result = taskCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("task1", result[0].id)
            assertEquals("projectA", result[0].projectName)
            assertEquals("Title 1", result[0].title)
            assertEquals("Description 1", result[0].description)
            assertEquals("state1", result[0].stateId)
            assertEquals("2023-01-01", result[0].createdDate)
            assertEquals("2023-01-02", result[0].updatedDate)
            assertEquals("task2", result[1].id)
            assertEquals("projectB", result[1].projectName)
            assertEquals("Title 2", result[1].title)
            assertEquals("Description 2", result[1].description)
            assertEquals("state2", result[1].stateId)
            assertEquals("2023-02-01", result[1].createdDate)
            assertEquals("2023-02-02", result[1].updatedDate)
        }

        @Test
        fun `read should create file when not exist`() = runTest {
            tempFile.delete()

            val result = taskCSVReaderWriter.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should throw exception when file creation fails`() = runTest {
            tempFile.delete()
            tempFile.mkdir()

            assertThrows<IOException> { taskCSVReaderWriter.read() }
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            tempFile.writeText("content")
            tempFile.setReadable(false)

            val result = taskCSVReaderWriter.read()

            assertThat(result).isEmpty()
            tempFile.setReadable(true)
        }

        @Test
        fun `read should throw exception on invalid CSV`() = runTest {
            File(testFilePath).writeText("invalid,format\n1,2,3")

            assertThrows<Throwable> { taskCSVReaderWriter.read() }
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `overWrite should create file with correct content`() = runTest {
            val tasks = listOf(
                TaskDto("task1", "projectA", "Title 1", "Description 1", "state1", "2023-01-01", "2023-01-02"),
                TaskDto("task2", "projectB", "Title 2", "Description 2", "state2", "2023-02-01", "2023-02-02")
            )

            val result = taskCSVReaderWriter.overWrite(tasks)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("task1,projectA,Title 1,Description 1,state1,2023-01-01,2023-01-02"))
            assertTrue(content.contains("task2,projectB,Title 2,Description 2,state2,2023-02-01,2023-02-02"))
        }

        @Test
        fun `overWrite should handle empty list`() = runTest {
            val result = taskCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,projectName,title,description,stateId,createdDate,updatedDate", content.trim())
        }
    }

    @Nested
    inner class AppendTests {
        @Test
        fun `append should add records to empty file`() = runTest {
            val tasks = listOf(
                TaskDto("task1", "projectA", "Title 1", "Description 1", "state1", "2023-01-01", "2023-01-02"),
                TaskDto("task2", "projectB", "Title 2", "Description 2", "state2", "2023-02-01", "2023-02-02")
            )

            val result = taskCSVReaderWriter.append(tasks)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("task1,projectA,Title 1,Description 1,state1,2023-01-01,2023-01-02"))
            assertTrue(content.contains("task2,projectB,Title 2,Description 2,state2,2023-02-01,2023-02-02"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialTasks = listOf(
                TaskDto("task1", "projectA", "Title 1", "Description 1", "state1", "2023-01-01", "2023-01-02")
            )
            taskCSVReaderWriter.overWrite(initialTasks)

            val newTasks = listOf(
                TaskDto("task2", "projectB", "Title 2", "Description 2", "state2", "2023-02-01", "2023-02-02")
            )

            val result = taskCSVReaderWriter.append(newTasks)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("task1,projectA,Title 1,Description 1,state1,2023-01-01,2023-01-02"))
            assertTrue(content.contains("task2,projectB,Title 2,Description 2,state2,2023-02-01,2023-02-02"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = taskCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,projectName,title,description,stateId,createdDate,updatedDate", content.trim())
        }

        @Test
        fun `append should create file when not exist`() = runTest {
            tempFile.delete()

            val tasks = listOf(
                TaskDto("task1", "projectA", "Title 1", "Description 1", "state1", "2023-01-01", "2023-01-02")
            )

            val result = taskCSVReaderWriter.append(tasks)
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("task1,projectA,Title 1,Description 1,state1,2023-01-01,2023-01-02"))
        }
    }
}