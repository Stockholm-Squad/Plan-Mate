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
    private lateinit var dataSource: ProjectCSVReaderWriter

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("tst", ".csv").toFile()
        testFilePath = tempFile.path
        dataSource = ProjectCSVReaderWriter(testFilePath)
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

            val result = dataSource.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return empty list when file is empty`() = runTest {
            File(testFilePath).writeText("")

            val result = dataSource.read()

            assertThat(tempFile.exists()).isTrue()
            assertThat(result).isEmpty()
        }

        @Test
        fun `read should return empty list when file has only header`() = runTest {
            File(testFilePath).writeText("id,name,description")

            val result = dataSource.read()
            assertTrue(result.isEmpty())
        }

        @Test
        fun `read should return projects when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                id,name,stateId
                1,Project A,State A
                2,Project B,State B\n
            """.trimIndent()
            )

            val result = dataSource.read()

            assertEquals(2, result.size)
            assertEquals("Project A", result[0].name)
            assertEquals("Project B", result[1].name)
        }

        @Test
        fun `read should return empty list when creating new file`() = runTest {
            // Ensure file doesn't exist initially
            tempFile.delete()

            val result = dataSource.read()

            assertThat(result).isEmpty()
            assertThat(tempFile.exists()).isTrue()
        }

        @Test
        fun `read should return empty list when file creation fails`() = runTest {
            // Create a directory with the same name to prevent file creation
            tempFile.delete()
            tempFile.mkdir() // This will make createNewFile() fail


            assertThrows<IOException> {dataSource.read()}
            assertThat(tempFile.exists()).isTrue()
            assertThat(tempFile.isDirectory).isTrue()
        }

        @Test
        fun `read should return empty list when file cannot be read`() = runTest {
            tempFile.writeText("content")
            tempFile.setReadable(false)

            val result = dataSource.read()

            assertThat(result).isEmpty()
            tempFile.setReadable(true)
        }
    }

    @Nested
    inner class WriteTests {

        @Test
        fun `write should create file with correct content`() = runTest {
            val projects = listOf(
                ProjectDto(id = "1", name = "Project A", stateId = "State A"),
                ProjectDto(id = "2", name = "Project B", stateId = "State B")
            )

            val result = dataSource.overWrite(projects)
            assertTrue(result)

            val content = File(testFilePath).readText()

            // Check for the actual fields being written
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `write should handle empty list`() = runTest {
            val result = dataSource.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,name,stateId", content.trim())
        }

        @Test
        fun `read should return ReadException on invalid CSV`() = runTest {
            File(testFilePath).writeText("invalid,csv,data\nnot,matching,fields\n1")


            assertThrows<Throwable> { dataSource.read() }
        }

    }

    @Nested
    inner class AppendTests {

        @Test
        fun `append should add records to empty file`() = runTest {
            val projects = listOf(
                ProjectDto(id = "1", name = "Project A", stateId = "State A"),
                ProjectDto(id = "2", name = "Project B", stateId = "State B")
            )

            val result = dataSource.append(projects)

            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `append should add records to existing file`() = runTest {
            val initialProjects = listOf(
                ProjectDto(id = "1", name = "Project A", stateId = "State A")
            )
            dataSource.overWrite(initialProjects)

            val newProjects = listOf(
                ProjectDto(id = "2", name = "Project B", stateId = "State B")
            )

            val result = dataSource.append(newProjects)

            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `append should do nothing with empty list`() = runTest {
            val result = dataSource.append(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertEquals("id,name,stateId", content.trim())
        }

        @Test
        fun `append should create file when not exist and write data`() = runTest {
            // Ensure file doesn't exist
            tempFile.delete()

            val projects = listOf(
                ProjectDto(id = "1", name = "Project A", stateId = "State A"),
                ProjectDto(id = "2", name = "Project B", stateId = "State B")
            )

            val result = dataSource.append(projects)

            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `append should create file when not exist and handle empty list`() = runTest {
            // Ensure file doesn't exist
            tempFile.delete()

            val result = dataSource.append(emptyList())

            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertEquals("id,name,stateId", content.trim())
        }
    }

}