package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.ProjectDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.ProjectCSVReaderWriter
import org.junit.jupiter.api.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProjectCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var projectCSVReaderWriter: ProjectCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("tst", ".csv").toFile()
        testFilePath = tempFile.path
        projectCSVReaderWriter = ProjectCSVReaderWriter(testFilePath)
    }

    @AfterEach
    fun tearDown() {
        // Delete the test file after each test
        File(testFilePath).takeIf { it.exists() }?.delete()
    }

    @Nested
    inner class ReadTests {
        @Test
        fun `read should return FileNotExistException when file doesn't exist`() = runTest {
            File(testFilePath).delete()

            val result = projectCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = projectCSVReaderWriter.read()

            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("id,title,description")

            val result = projectCSVReaderWriter.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return projects when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                id,stateId,title
                1,State A,Project A
                2,State B,Project B
            """.trimIndent()
            )

            val result = projectCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("Project A", result[0].title)
            assertEquals("Project B", result[1].title)
        }

        @Test
        fun `read should return empty list when creating new file`() = runTest {
            // Ensure file doesn't exist initially
            tempFile.delete()

            val result = projectCSVReaderWriter.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should return empty list when file creation fails`() = runTest {
            // Create a directory with the same title to prevent file creation
            tempFile.delete()
            tempFile.mkdir() // This will make createNewFile() fail


            assertThrows<IOException> {projectCSVReaderWriter.read()}
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            tempFile.writeText("content")
            tempFile.setReadable(false)

            val result = projectCSVReaderWriter.read()

            assertThat(result).isEmpty()
            tempFile.setReadable(true)
        }
    }

    @Nested
    inner class WriteTests {

        @Test
        fun `write should create file with correct content`() = runTest {
            val projects = listOf(
                ProjectDto(id = "1", title = "Project A", stateId = "State A"),
                ProjectDto(id = "2", title = "Project B", stateId = "State B")
            )

            val result = projectCSVReaderWriter.overWrite(projects)
            assertTrue(result)

            val content = File(testFilePath).readText()

            // Check for the actual fields being written
            assertTrue(content.contains("1,State A,Project A"))
            assertTrue(content.contains("2,State B,Project B"))
        }

        @Test
        fun `write should handle empty list`() = runTest {
            val result = projectCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,stateId,title", content.trim())
        }

        @Test
        fun `read should return ReadException on invalid CSV`() = runTest {
            File(testFilePath).writeText("invalid,csv,data\nnot,matching,fields\n1")


            assertThrows<Throwable> { projectCSVReaderWriter.read() }
        }

    }

    @Nested
    inner class AppendTests {

        @Test
        fun `append should add records to empty file`() = runTest {
            val projects = listOf(
                ProjectDto(id = "1", title = "Project A", stateId = "State A"),
                ProjectDto(id = "2", title = "Project B", stateId = "State B")
            )

            val result = projectCSVReaderWriter.append(projects)

            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,State A,Project A"))
            assertTrue(content.contains("2,State B,Project B"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialProjects = listOf(
                ProjectDto(id = "1", title = "Project A", stateId = "State A")
            )
            projectCSVReaderWriter.overWrite(initialProjects)

            val newProjects = listOf(
                ProjectDto(id = "2", title = "Project B", stateId = "State B")
            )

            val result = projectCSVReaderWriter.append(newProjects)

            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,State A,Project A"))
            assertTrue(content.contains("2,State B,Project B"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = projectCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,stateId,title", content.trim())
        }

        @Test
        fun `append should create file when not exist and write data`() = runTest {
            // Ensure file doesn't exist
            tempFile.delete()

            val projects = listOf(
                ProjectDto(id = "1", title = "Project A", stateId = "State A"),
                ProjectDto(id = "2", title = "Project B", stateId = "State B")
            )

            val result = projectCSVReaderWriter.append(projects)

            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("1,State A,Project A"))
            assertTrue(content.contains("2,State B,Project B"))
        }

        @Test
        fun `append should create file when not exist and handle empty list`() = runTest {
            // Ensure file doesn't exist
            tempFile.delete()

            val result = projectCSVReaderWriter.append(emptyList())

            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertEquals("id,stateId,title", content.trim())
        }
    }

}