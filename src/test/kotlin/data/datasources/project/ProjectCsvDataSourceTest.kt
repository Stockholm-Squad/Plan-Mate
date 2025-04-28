package data.datasources.project

import com.google.common.truth.Truth.assertThat
import org.example.data.datasources.project.ProjectCsvDataSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import utils.mockInvalidFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

class ProjectCsvDataSourceTest {

    private lateinit var tempFile: File
    private lateinit var projectCsvDataSource: ProjectCsvDataSource
    private lateinit var invalidFile: File
    private lateinit var projectCsvDataSourceWithInvalidFile: ProjectCsvDataSource
    @BeforeEach
    fun setUp() {
        // Create a temporary file for testing
        tempFile = Files.createTempFile("projects", ".csv").toFile()
        projectCsvDataSource = ProjectCsvDataSource(tempFile)

        invalidFile = mockInvalidFile()
        projectCsvDataSourceWithInvalidFile = ProjectCsvDataSource(invalidFile)
    }

    @AfterEach
    fun tearDown() {
        // Clean up the temporary file after each test
        tempFile.delete()
    }

    @Test
    fun `getAllProjects() should return a list of projects when data is available`() {
        val project1 = buildProject(name = "1", stateId = "1")
        val project2 = buildProject(name = "2", stateId = "2")
        val project3 = buildProject(name = "3", stateId = "3")

        // Given
        tempFile.writeText(
            """
            ${project1.id},${project1.name},${project1.stateId}
            ${project2.id},${project2.name},${project2.stateId}
            ${project3.id},${project3.name},${project3.stateId}
            """.trimIndent()
        )

        // When
        val result = projectCsvDataSource.getAllProjects()

        // Then
        assertThat(result.getOrThrow()).containsExactly(project1, project2, project3)

    }

    @Test
    fun `getAllProjects() should return an empty list when the file is empty`() {
        // Given
        tempFile.writeText("") // Empty file

        // When
        val result = projectCsvDataSource.getAllProjects()

        // Then
        assertThat(result.getOrThrow()).isEmpty()

    }

    @Test
    fun `getAllProjects() should return failure when there is an error reading the file`() {
        // Given
        tempFile.delete() // Delete the file to simulate a missing file

        // When
        val result = projectCsvDataSource.getAllProjects()

        // Then
        assertThrows<Throwable>{result.getOrThrow()} //TODO Custom Exception
    }

    // Tests for addProject()
    @Test
    fun `addProject() should successfully add a new project to the file`() {
        // Given
        val project = buildProject(
            name = "New Project",
            stateId = "state-3"
        )

        // When
        val result = projectCsvDataSource.addProject(project)

        // Then
        assertThat(result.getOrThrow()).isTrue().also {
            val fileContent = tempFile.readLines()
            assertThat(fileContent).hasSize(1)
            assertThat(fileContent[0]).isEqualTo("${project.id},${project.name},${project.stateId}")
        }
    }

    @Test
    fun `addProject() should return failure when there is an error writing to the file`() {
        // Given
        tempFile.delete() // Delete the file to simulate a missing file

        val project = buildProject()

        // When
        val result = projectCsvDataSource.addProject(project)

        // Then
        assertThrows<Throwable>{result.getOrThrow()} //TODO Custom Exception

    }


    @Test
    fun `getProjectById() should return a project when the ID exists`() {
        // Given
        val project1 = buildProject(
            name = "Project 1",
            stateId = "state-4"
        )
        tempFile.writeText(
            """
            ${project1.id},${project1.name},${project1.stateId}
            """.trimIndent()
        )

        // When
        val result = projectCsvDataSource.getProjectById(project1.id)

        // Then
        assertThat(result.getOrThrow()).isEqualTo(project1)
    }

    @Test
    fun `getProjectById() should return a Failure when the ID don't exists`() {
        // Given
        val project1 = buildProject(
            name = "Project 1",
            stateId = "state-4"
        )
        val project2 = buildProject()
        tempFile.writeText(
            """
            ${project1.id},${project1.name},${project1.stateId}
            """.trimIndent()
        )

        // When
        val result = projectCsvDataSource.getProjectById(project2.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `getProjectById() should return failure when the ID does not exist in empty file`() {
        // Given
        val project = buildProject()
        tempFile.writeText("")

        // When
        val result = projectCsvDataSource.getProjectById(project.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom Exception
    }

    @Test
    fun `editProject() should successfully update an existing project`() {
        // Given
        val project = buildProject(
            name = "Old Name",
            stateId = "Old State"
        )

        tempFile.writeText(
            """
            ${project.id},${project.name},${project.stateId}
            """.trimIndent()
        )

        val updatedProject = buildProject(
            id = project.id,
            name = "New Name",
            stateId = "New state"
        )

        // When
        val result = projectCsvDataSource.editProject(updatedProject)

        // Then
        assertThat(result.getOrThrow()).isTrue().also {
            val fileContent = tempFile.readLines()
            assertThat(fileContent).hasSize(1)
            assertThat(fileContent[0]).isEqualTo("${project.id},${updatedProject.name},${updatedProject.stateId}")
        }
    }

    @Test
    fun `editProject() should return failure when the project does not exist in empty file`() {
        // Given
        val project = buildProject()
        tempFile.writeText("")

        val updatedProject = buildProject(
            id = project.id,
            name = "Updated Project",
            stateId = "state-6"
        )

        // When
        val result = projectCsvDataSource.editProject(updatedProject)

        // Then
        assertThrows<Throwable> {result.getOrThrow()} // TODO: custom Exception
    }

    @Test
    fun `editProject() should return failure when the project does not exist and the file has another projects`() {
        // Given
        val project = buildProject(
            name = "Updated Project",
            stateId = "state-6"
        )

        tempFile.writeText(
            """
            ${project.id},${project.name},${project.stateId}
            """.trimIndent()
        )

        val project2 = buildProject()


        // When
        val result = projectCsvDataSource.editProject(project2)

        // Then
        assertThrows<Throwable> {result.getOrThrow()} // TODO: custom Exception
    }


    @Test
    fun `deleteProject() should successfully delete an existing project`() {
        // Given
        val project = buildProject(
            name = "project 22",
            stateId = "state 333"
        )
        tempFile.writeText(
            """
            ${project.id},${project.name},${project.stateId}
            """.trimIndent()
        )

        // When
        val result = projectCsvDataSource.deleteProject(project.id)

        // Then
        assertThat(result.getOrThrow()).isTrue().also {
            val fileContent = tempFile.readLines()
            assertThat(fileContent).isEmpty()
        }
    }

    @Test
    fun `deleteProject() should return failure when the project does not exist and the file contain another projects`() {
        // Given
        val project = buildProject(
            name = "project 22",
            stateId = "state 333"
        )
        tempFile.writeText(
            """
            ${project.id},${project.name},${project.stateId}
            """.trimIndent()
        )
        val project2 = buildProject()

        // When
        val result = projectCsvDataSource.deleteProject(project2.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `deleteProject() should return failure when the project does not exist in empty file`() {
        // Given
        val project = buildProject()
        tempFile.writeText("")

        // When
        val result = projectCsvDataSource.deleteProject(project.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `getProjectById() should return failure when the file does not exist`() {
        // Given
        tempFile.delete() // Delete the file to simulate a missing file
        val project = buildProject()

        // When
        val result = projectCsvDataSource.getProjectById(project.id)

        // Then
        assertThrows<Throwable> {assertThat(result.getOrThrow())} // TODO: custom exception
    }

    @Test
    fun `getProjectById() should handle invalid CSV format`() {
        // Given
        tempFile.writeText(
            """
            invalid-line
            """.trimIndent()
        )
        val project = buildProject()

        // When
        val result = projectCsvDataSource.getProjectById(project.id)

        // Then
        assertThrows<Throwable> {result.getOrThrow()} // TODO: custom exception
    }

    // Test for file existence check in addProject
    @Test
    fun `addProject() should return failure when the file does not exist`() {
        // Given
        tempFile.delete() // Delete the file to simulate a missing file
        val project = buildProject()

        // When
        val result = projectCsvDataSource.addProject(project)

        // Then
        assertThat(result.isFailure).isTrue()
        result.onFailure { cause ->
            assertThat(cause).isInstanceOf(FileNotFoundException::class.java)
            assertThat(cause.message).isEqualTo("File does not exist: ${tempFile.absolutePath}")
        }
    }

    // Test for file existence check in editProject
    @Test
    fun `editProject() should return failure when the file does not exist`() {
        // Given
        tempFile.delete() // Delete the file to simulate a missing file
        val project = buildProject()

        // When
        val result = projectCsvDataSource.editProject(project)

        // Then
        assertThrows<Throwable> { result.getOrThrow()} // TODO: custom exception
    }

    @Test
    fun `editProject() should handle invalid CSV format`() {
        // Given
        tempFile.writeText(
            """
            invalid-line
            """.trimIndent()
        )
        val project = buildProject()

        // When
        val result = projectCsvDataSource.editProject(project)

        // Then
        assertThrows<Throwable> { result.getOrThrow()} // TODO: custom exception
    }

    @Test
    fun `deleteProject() should return failure when the file does not exist`() {
        // Given
        tempFile.delete()
        val project = buildProject()

        // When
        val result = projectCsvDataSource.deleteProject(project.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow()} // TODO: custom exception
    }

    @Test
    fun `deleteProject() should handle invalid CSV format`() {
        // Given
        tempFile.writeText(
            """
            invalid-line
            """.trimIndent()
        )
        val project = buildProject()

        // When
        val result = projectCsvDataSource.deleteProject(project.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow()} // TODO: custom exception
    }

    @Test
    fun `getAllProjects() should return failure when the file does not exist`() {
        // Given
        tempFile.delete()

        // When
        val result = projectCsvDataSource.getAllProjects()

        // Then
        assertThrows<Throwable> { result.getOrThrow()} // TODO: custom exception
    }

    @Test
    fun `getAllProjects() should handle invalid CSV format`() {
        // Given
        tempFile.writeText(
            """
            invalid-line
            """.trimIndent()
        )

        // When
        val result = projectCsvDataSource.getAllProjects()

        // Then
        assertThat(result.getOrThrow()).isEmpty()
    }

    @Test
    fun `getProjectById() should catch Throwable exceptions when Invalid File`() {
        // Given
        val project = buildProject()

        // When
        val result = projectCsvDataSourceWithInvalidFile.getProjectById(project.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `addProject() should catch Throwable exceptions when Invalid File`() {
        // Given
        val project = buildProject()

        // When
        val result = projectCsvDataSourceWithInvalidFile.addProject(project)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `editProject() should catch Throwable exceptions when Invalid File`() {
        // Given
        val project = buildProject()

        // When
        val result = projectCsvDataSourceWithInvalidFile.editProject(project)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `deleteProject() should catch Throwable exceptions when Invalid File`() {
        // Given
        val project = buildProject()

        // When
        val result = projectCsvDataSourceWithInvalidFile.deleteProject(project.id)

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }

    @Test
    fun `getAllProjects() should catch Throwable exceptions when Invalid File`() {
        // Given

        // When
        val result = projectCsvDataSourceWithInvalidFile.getAllProjects()

        // Then
        assertThrows<Throwable> { result.getOrThrow() } // TODO: custom exception
    }
}