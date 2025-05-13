package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.UserAssignedToProjectDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.UserAssignedToProjectCSVReaderWriter
import org.junit.jupiter.api.*
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserAssignedToProjectCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var userAssignedToProjectCSVReaderWriter: UserAssignedToProjectCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("user_project_test", ".csv").toFile()
        testFilePath = tempFile.path
        userAssignedToProjectCSVReaderWriter = UserAssignedToProjectCSVReaderWriter(testFilePath)
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

            val result = userAssignedToProjectCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = userAssignedToProjectCSVReaderWriter.read()
            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("username,projectId")

            val result = userAssignedToProjectCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return assignments when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                username,projectId
                user1,projectA
                user2,projectB
                """.trimIndent()
            )

            val result = userAssignedToProjectCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("user1", result[0].username)
            assertEquals("projectA", result[0].projectId)
            assertEquals("user2", result[1].username)
            assertEquals("projectB", result[1].projectId)
        }

        @Test
        fun `read should create file when not exist`() = runTest {
            tempFile.delete()

            val result = userAssignedToProjectCSVReaderWriter.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should not throw exception when file creation fails (creates empty file)`() = runTest {
            tempFile.delete()
            tempFile.mkdir()

            assertThrows<Throwable> { userAssignedToProjectCSVReaderWriter.read() }
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            tempFile.writeText("content")
            tempFile.setReadable(false)

            val result = userAssignedToProjectCSVReaderWriter.read()

            assertThat(result).isEmpty()
            tempFile.setReadable(true)
        }

        @Test
        fun `read should return empty list on invalid CSV (less than 2 lines)`() = runTest {
            File(testFilePath).writeText("invalid,format")

            val result = userAssignedToProjectCSVReaderWriter.read()
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `overWrite should create file with correct content`() = runTest {
            val assignments = listOf(
                UserAssignedToProjectDto("user1", "projectA"),
                UserAssignedToProjectDto("user2", "projectB")
            )

            val result = userAssignedToProjectCSVReaderWriter.overWrite(assignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,projectA"))
            assertTrue(content.contains("user2,projectB"))
        }

        @Test
        fun `overWrite should handle empty list`() = runTest {
            val result = userAssignedToProjectCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("username,projectId", content.trim())
        }
    }

    @Nested
    inner class AppendTests {
        @Test
        fun `append should add records to empty file`() = runTest {
            val assignments = listOf(
                UserAssignedToProjectDto("user1", "projectA"),
                UserAssignedToProjectDto("user2", "projectB")
            )

            val result = userAssignedToProjectCSVReaderWriter.append(assignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,projectA"))
            assertTrue(content.contains("user2,projectB"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialAssignments = listOf(
                UserAssignedToProjectDto("user1", "projectA")
            )
            userAssignedToProjectCSVReaderWriter.overWrite(initialAssignments)

            val newAssignments = listOf(
                UserAssignedToProjectDto("user2", "projectB")
            )

            val result = userAssignedToProjectCSVReaderWriter.append(newAssignments)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,projectA"))
            assertTrue(content.contains("user2,projectB"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = userAssignedToProjectCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("username,projectId", content.trim())
        }

        @Test
        fun `append should create file when not exist`() = runTest {
            tempFile.delete()

            val assignments = listOf(
                UserAssignedToProjectDto("user1", "projectA")
            )

            val result = userAssignedToProjectCSVReaderWriter.append(assignments)
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("user1,projectA"))
        }
    }
}