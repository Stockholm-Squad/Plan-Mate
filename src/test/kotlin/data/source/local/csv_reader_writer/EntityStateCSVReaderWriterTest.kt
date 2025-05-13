package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.EntityStateDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.EntityStateCSVReaderWriter
import org.junit.jupiter.api.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EntityStateCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var entityStateCSVReaderWriter: EntityStateCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("entity_state_test", ".csv").toFile()
        testFilePath = tempFile.path
        entityStateCSVReaderWriter = EntityStateCSVReaderWriter(testFilePath)
    }

    @AfterEach
    fun tearDown() {
        // Delete the test file after each test
        File(testFilePath).takeIf { it.exists() }?.delete()
    }

    @Nested
    inner class ReadTests {
        @Test
        fun `read should return empty list when file doesn't exist`() = runTest {
            File(testFilePath).delete()

            val result = entityStateCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = entityStateCSVReaderWriter.read()

            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("id,title")

            val result = entityStateCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return entity states when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                id,title
                1,State A
                2,State B
            """.trimIndent()
            )

            val result = entityStateCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("State A", result[0].title)
            assertEquals("State B", result[1].title)
        }

        @Test
        fun `read should return empty list when creating new file`() = runTest {
            // Ensure file doesn't exist initially
            tempFile.delete()

            val result = entityStateCSVReaderWriter.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should return empty list when file creation fails`() = runTest {
            // Create a directory with the same title to prevent file creation
            tempFile.delete()
            tempFile.mkdir() // This will make createNewFile() fail

            assertThrows<IOException> { entityStateCSVReaderWriter.read() }
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            tempFile.writeText("content")
            tempFile.setReadable(false)

            val result = entityStateCSVReaderWriter.read()

            assertThat(result).isEmpty()
            tempFile.setReadable(true)
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `write should create file with correct content`() = runTest {
            val states = listOf(
                EntityStateDto(id = "1", title = "State A"),
                EntityStateDto(id = "2", title = "State B")
            )

            val result = entityStateCSVReaderWriter.overWrite(states)
            assertTrue(result)

            val content = File(testFilePath).readText()

            assertTrue(content.contains("1,State A"))
            assertTrue(content.contains("2,State B"))
        }

        @Test
        fun `write should handle empty list`() = runTest {
            val result = entityStateCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,title", content.trim())
        }

        @Test
        fun `read should throw exception on invalid CSV`() = runTest {
            File(testFilePath).writeText("invalid,csv,data\nnot,matching,fields\n1")

            assertThrows<Throwable> { entityStateCSVReaderWriter.read() }
        }
    }

    @Nested
    inner class AppendTests {
        @Test
        fun `append should add records to empty file`() = runTest {
            val states = listOf(
                EntityStateDto(id = "1", title = "State A"),
                EntityStateDto(id = "2", title = "State B")
            )

            val result = entityStateCSVReaderWriter.append(states)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,State A"))
            assertTrue(content.contains("2,State B"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialStates = listOf(
                EntityStateDto(id = "1", title = "State A")
            )
            entityStateCSVReaderWriter.overWrite(initialStates)

            val newStates = listOf(
                EntityStateDto(id = "2", title = "State B")
            )

            val result = entityStateCSVReaderWriter.append(newStates)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,State A"))
            assertTrue(content.contains("2,State B"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = entityStateCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,title", content.trim())
        }

        @Test
        fun `append should create file when not exist and write data`() = runTest {
            // Ensure file doesn't exist
            tempFile.delete()

            val states = listOf(
                EntityStateDto(id = "1", title = "State A"),
                EntityStateDto(id = "2", title = "State B")
            )

            val result = entityStateCSVReaderWriter.append(states)
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("1,State A"))
            assertTrue(content.contains("2,State B"))
        }

        @Test
        fun `append should create file when not exist and handle empty list`() = runTest {
            // Ensure file doesn't exist
            tempFile.delete()

            val result = entityStateCSVReaderWriter.append(emptyList())
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertEquals("id,title", content.trim())
        }
    }
}