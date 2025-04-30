package data.datasources

import logic.model.entities.Project
import org.example.data.datasources.ProjectCsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProjectCsvDataSourceTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var dataSource: ProjectCsvDataSource

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("tst", ".csv").toFile()
        testFilePath = tempFile.path
        dataSource = ProjectCsvDataSource(testFilePath)
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
            assertThrows<PlanMateExceptions.DataException.FileNotExistException> { result.getOrThrow()}
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
                Project(id = "1", name = "Project A", stateId = "State A"),
                Project(id = "2", name = "Project B", stateId = "State B")
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
    }
}