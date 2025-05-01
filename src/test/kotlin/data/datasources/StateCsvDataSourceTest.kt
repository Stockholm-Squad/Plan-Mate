package data.datasources

import logic.model.entities.State
import org.example.data.datasources.StateCsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.*
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertTrue

class StateCsvDataSourceTest {
    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var dataSource: StateCsvDataSource

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("tst", ".csv").toFile()
        testFilePath = tempFile.path
        dataSource = StateCsvDataSource(testFilePath)
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
            assertThrows<PlanMateExceptions.DataException.FileNotExistException> { result.getOrThrow() }
        }

        @Test
        fun `read should return Throwable when read from file not exist`() {
            File(testFilePath).writeText("")
            dataSource = StateCsvDataSource("")
            val result = dataSource.read()
            assertThrows<Throwable> { result.getOrThrow() }
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
            File(testFilePath).writeText("id,name")

            val result = dataSource.read()
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

        @Test
        fun `read should return states when file contains valid data`() {
            File(testFilePath).writeText(
                """
                id,name
                1,TODO,
                2,PROGRESS,\n
            """.trimIndent()
            )

            val result = dataSource.read()
            println(result)
            assertTrue(result.isSuccess)
            val states = result.getOrThrow()
            Assertions.assertEquals(2, states.size)
            Assertions.assertEquals("TODO", states[0].name)
            Assertions.assertEquals("PROGRESS", states[1].name)
        }
    }

    @Nested
    inner class WriteTests {

        @Test
        fun `write should create file with correct content`() {
            val states = listOf(
                State(id = "1", name = "TODO"),
                State(id = "2", name = "Progress")
            )

            val result = dataSource.overWrite(states)
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            println("File content:\n$content") // Debug output
            println(content)
            // Check for the actual fields being written
            assertTrue(content.contains("1,TODO"))
            assertTrue(content.contains("2,Progress"))
        }

        @Test
        fun `write should handle empty list`() {
            val result = dataSource.overWrite(emptyList())
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            Assertions.assertEquals("", content)
        }
    }
}