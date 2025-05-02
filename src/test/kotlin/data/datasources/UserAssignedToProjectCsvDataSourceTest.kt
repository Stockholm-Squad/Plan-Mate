package data.datasources

import org.example.data.datasources.relations.user_assigned_to_project_data_source.UserAssignedToProjectCsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserAssignedToProjectCsvDataSourceTest {

    private lateinit var tempFile: File
    private lateinit var dataSource: UserAssignedToProjectCsvDataSource

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("user_assigned_test", ".csv").toFile()
        dataSource = UserAssignedToProjectCsvDataSource(tempFile.path)
    }

    @AfterEach
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun `read should return FileNotExistException when file doesn't exist`() {
        tempFile.delete()
        val result = dataSource.read()
        assertTrue(result.isFailure)
        assertFailsWith<PlanMateExceptions.DataException.FileNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `read should return empty list when file is empty`() {
        tempFile.writeText("")
        val result = dataSource.read()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `read should return empty list when file has only header`() {
        tempFile.writeText("userName,projectId")
        val result = dataSource.read()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `read should return records when file contains valid data`() {
        tempFile.writeText("""
            userName,projectId
            user1,P1
            user2,P2
        """.trimIndent())

        val result = dataSource.read()
        assertTrue(result.isSuccess)
        val users = result.getOrThrow()
        assertEquals(2, users.size)
        assertEquals("user1", users[0].userName)
        assertEquals("P2", users[1].projectId)
    }

    @Test
    fun `read should return ReadException on invalid CSV`() {
        tempFile.writeText("invalid\nbad,data\n123")
        val result = dataSource.read()
        assertTrue(result.isFailure)
        assertFailsWith<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }
}
