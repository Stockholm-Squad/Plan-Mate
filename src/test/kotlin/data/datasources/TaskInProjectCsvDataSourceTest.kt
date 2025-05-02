package data.datasources

import org.junit.jupiter.api.Assertions.*

import org.example.data.datasources.relations.task_In_project_data_source.TaskInProjectCsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskInProjectCsvDataSourceTest {

    private lateinit var tempFile: File
    private lateinit var dataSource: TaskInProjectCsvDataSource

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("task_in_project_test", ".csv").toFile()
        dataSource = TaskInProjectCsvDataSource(tempFile.path)
    }

    @AfterEach
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun `read should return FileNotExistException when file doesn't exist`() {
        tempFile.delete()

        val result = dataSource.read()

        assertThrows<PlanMateExceptions.DataException.FileNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `read should return empty list when file is empty`() {
        tempFile.writeText("")

        val result = dataSource.read()

        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `read should return empty list when file has only header`() {
        tempFile.writeText("taskId,projectId")

        val result = dataSource.read()

        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `read should return records when file contains valid data`() {
        tempFile.writeText("""
            taskId,projectId
            T1,P1
            T2,P2
        """.trimIndent())

        val result = dataSource.read()

        assertTrue(result.isSuccess)
        val tasks = result.getOrThrow()
        assertEquals(2, tasks.size)
        assertEquals("T1", tasks[0].taskId)
        assertEquals("P2", tasks[1].projectId)
    }

    @Test
    fun `read should return ReadException on invalid CSV`() {
        tempFile.writeText("invalid\nbad,data\n123")
        val result = dataSource.read()
        assertTrue(result.isFailure)
        assertFailsWith<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }
}
