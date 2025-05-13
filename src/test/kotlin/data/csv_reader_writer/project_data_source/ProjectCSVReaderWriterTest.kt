package data.csv_reader_writer.project_data_source

import logic.models.exceptions.FileNotExistException
import logic.models.exceptions.ReadDataException
import org.example.data.csv_reader_writer.project.ProjectCSVReaderWriter
import data.dto.ProjectDto
import org.junit.jupiter.api.*
import java.io.File
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
        fun `read should return FileNotExistException when file doesn't exist`() {
            File(testFilePath).delete()

            val result = dataSource.read()

            assertTrue(result.isFailure)
            assertThrows<FileNotExistException> { result.getOrThrow() }
        }

        @Test
        fun `read should return empty list when file is empty`() {
            File(testFilePath).writeText("")

            val result = dataSource.read()
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

        @Test
        fun `read should return empty list when file has only header`() {
            File(testFilePath).writeText("id,name,description")

            val result = dataSource.read()
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

        @Test
        fun `read should return projects when file contains valid data`() {
            File(testFilePath).writeText(
                """
                id,name,stateId
                1,Project A,State A
                2,Project B,State B\n
            """.trimIndent()
            )

            val result = dataSource.read()
            println(result)
            assertTrue(result.isSuccess)
            val projects = result.getOrThrow()
            assertEquals(2, projects.size)
            assertEquals("Project A", projects[0].name)
            assertEquals("Project B", projects[1].name)
        }
    }

    @Nested
    inner class WriteTests {

        @Test
        fun `write should create file with correct content`() {
            val projects = listOf(
                ProjectDto(id = "1", name = "Project A", title = "State A"),
                ProjectDto(id = "2", name = "Project B", title = "State B")
            )

            val result = dataSource.overWrite(projects)
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            println("File content:\n$content") // Debug output
            println(content)
            // Check for the actual fields being written
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `write should handle empty list`() {
            val result = dataSource.overWrite(emptyList())
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            assertEquals("", content)
        }

        @Test
        fun `read should return ReadException on invalid CSV`() {
            File(testFilePath).writeText("invalid,csv,data\nnot,matching,fields\n1")

            val result = dataSource.read()

            assertTrue(result.isFailure)
            assertThrows<ReadDataException> { result.getOrThrow() }
        }

    }

    @Nested
    inner class AppendTests {

        @Test
        fun `append should add records to empty file`() {
            val projects = listOf(
                ProjectDto(id = "1", name = "Project A", title = "State A"),
                ProjectDto(id = "2", name = "Project B", title = "State B")
            )

            val result = dataSource.append(projects)

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `append should add records to existing file`() {
            val initialProjects = listOf(
                ProjectDto(id = "1", name = "Project A", title = "State A")
            )
            dataSource.overWrite(initialProjects)

            val newProjects = listOf(
                ProjectDto(id = "2", name = "Project B", title = "State B")
            )

            val result = dataSource.append(newProjects)

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            assertTrue(content.contains("1,Project A,State A"))
            assertTrue(content.contains("2,Project B,State B"))
        }

        @Test
        fun `append should do nothing with empty list`() {
            val result = dataSource.append(emptyList())
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            assertEquals("", content)
        }
    }

}