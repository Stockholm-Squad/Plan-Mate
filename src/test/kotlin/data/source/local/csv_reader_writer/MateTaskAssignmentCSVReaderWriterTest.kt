package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.MateTaskAssignmentDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.MateTaskAssignmentCSVReaderWriter
import org.junit.jupiter.api.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MateTaskAssignmentCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var mateTaskAssignmentCSVReaderWriter: MateTaskAssignmentCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("mate_task_test", ".csv").toFile()
        testFilePath = tempFile.path
        mateTaskAssignmentCSVReaderWriter = MateTaskAssignmentCSVReaderWriter(testFilePath)
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

            val result = mateTaskAssignmentCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = mateTaskAssignmentCSVReaderWriter.read()
            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("username,taskId")

            val result = mateTaskAssignmentCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return assignments when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                username,taskId
                user1,task1
                user2,task2
            """.trimIndent()
            )

            val result = mateTaskAssignmentCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("user1", result[0].username)
            assertEquals("task1", result[0].taskId)
            assertEquals("user2", result[1].username)
            assertEquals("task2", result[1].taskId)
        }

        @Test
        fun `read should create file when not exist`() = runTest {
            tempFile.delete()

            val result = mateTaskAssignmentCSVReaderWriter.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should throw exception when file creation fails`() = runTest {
            tempFile.delete()
            tempFile.mkdir()

            assertThrows<IOException> { mateTaskAssignmentCSVReaderWriter.read() }
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            tempFile.writeText("content")
            tempFile.setReadable(false)

            val result = mateTaskAssignmentCSVReaderWriter.read()

            assertThat(result).isEmpty()
            tempFile.setReadable(true)
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `overWrite should create file with correct content`() = runTest {
            val assignments = listOf(
                MateTaskAssignmentDto("user1", "task1"),
                MateTaskAssignmentDto("user2", "task2")
            )

            val result = mateTaskAssignmentCSVReaderWriter.overWrite(assignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,task1"))
            assertTrue(content.contains("user2,task2"))
        }

        @Test
        fun `overWrite should handle empty list`() = runTest {
            val result = mateTaskAssignmentCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("username,taskId", content.trim())
        }

        @Test
        fun `read should throw exception on invalid CSV`() = runTest {
            File(testFilePath).writeText("invalid,format\n1,2,3")

            assertThrows<Throwable> { mateTaskAssignmentCSVReaderWriter.read() }
        }
    }

    @Nested
    inner class AppendTests {
        @Test
        fun `append should add records to empty file`() = runTest {
            val assignments = listOf(
                MateTaskAssignmentDto("user1", "task1"),
                MateTaskAssignmentDto("user2", "task2")
            )

            val result = mateTaskAssignmentCSVReaderWriter.append(assignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,task1"))
            assertTrue(content.contains("user2,task2"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialAssignments = listOf(
                MateTaskAssignmentDto("user1", "task1")
            )
            mateTaskAssignmentCSVReaderWriter.overWrite(initialAssignments)

            val newAssignments = listOf(
                MateTaskAssignmentDto("user2", "task2")
            )

            val result = mateTaskAssignmentCSVReaderWriter.append(newAssignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,task1"))
            assertTrue(content.contains("user2,task2"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = mateTaskAssignmentCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("username,taskId", content.trim())
        }

        @Test
        fun `append should create file when not exist`() = runTest {
            tempFile.delete()

            val assignments = listOf(
                MateTaskAssignmentDto("user1", "task1")
            )

            val result = mateTaskAssignmentCSVReaderWriter.append(assignments)
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("user1,task1"))
        }
    }
}