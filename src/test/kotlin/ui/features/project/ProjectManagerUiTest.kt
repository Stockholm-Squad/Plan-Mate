package ui.features.project

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Project
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.buildProject

class ProjectManagerUiTest {
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private lateinit var stateManagerUi: AdminStateManagerUi
    private lateinit var taskManagerUi: TaskManagerUi
    private lateinit var CreateUserUi: CreateUserUi
    private lateinit var inputReader: InputReader
    private lateinit var outputPrinter: OutputPrinter
    private lateinit var projectManagerUi: ProjectManagerUi
    private lateinit var manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase


    @BeforeEach
    fun setUp() {
        manageProjectUseCase = mockk(relaxed = true)
        stateManagerUi = mockk(relaxed = true)
        taskManagerUi = mockk(relaxed = true)
        CreateUserUi = mockk(relaxed = true)
        inputReader = mockk(relaxed = true)
        outputPrinter = mockk(relaxed = true)
        manageUsersAssignedToProjectUseCase = mockk(relaxed = true)

        projectManagerUi = ProjectManagerUi(
            inputReader,
            outputPrinter,
            manageProjectUseCase,
            manageUsersAssignedToProjectUseCase,
            stateManagerUi,
            taskManagerUi,
            CreateUserUi,
        )
    }

    @Nested
    inner class ShowAllProjects {
        @Test
        fun `should display all projects with their IDs and names`() {
            // Given
            val projects = listOf(
                buildProject(id = "1", name = "Project A"),
                buildProject(id = "2", name = "Project B")
            )
            every { manageProjectUseCase.getAllProjects() } returns Result.success(projects)
            every { inputReader.readStringOrNull() } returnsMany listOf("1", "0")


            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("1 -> Project A") }
            verify { outputPrinter.showMessage("2 -> Project B") }
        }

        @Test
        fun `should display error when getting projects fails`() {
            // Given
            every { manageProjectUseCase.getAllProjects() } returns Result.failure(Exception("Database error"))
            every { inputReader.readStringOrNull() } returnsMany listOf("1", "0")

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Database error") }
        }

        @Test
        fun `should display message when no projects exist`() {
            // Given
            every { manageProjectUseCase.getAllProjects() } returns Result.success(emptyList())
            every { inputReader.readStringOrNull() } returnsMany listOf("1", "0")

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("No projects found") }
        }
    }

    @Nested
    inner class ShowProjectById {
        @Test
        fun `should display project details when project exists`() {
            // Given
            val project = buildProject(id = "1", name = "Project A")
            every { manageProjectUseCase.getProjectById("1") } returns Result.success(project)
            every { inputReader.readStringOrNull() } returnsMany listOf("2", "1", "0")

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("Project Details:") }
            verify { outputPrinter.showMessage("ID: 1") }
            verify { outputPrinter.showMessage("Name: Project A") }
        }

        @Test
        fun `should display error message when project does not exist`() {
            // Given
            every { manageProjectUseCase.getProjectById("999") } returns Result.failure(NoSuchElementException("Project not found"))
            every { inputReader.readStringOrNull() } returnsMany listOf("2", "999", "0")

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Project not found") }
        }
    }

    @Nested
    inner class AddProject {
        @Test
        fun `should successfully add project with valid name and state`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("3", "New Project", "state 1", "no", "0")
            every { stateManagerUi.showAllStates() } returns Unit
            every { manageProjectUseCase.addProject(any()) } returns Result.success(true)

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("Project added successfully") }
            verify { outputPrinter.showMessage("Enter project name: ") }
            verify { outputPrinter.showMessage("Enter state ID (or 'new' to create a new state): ") }
        }

        @Test
        fun `should prompt to create new state when requested`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("3", "New Project", "new", "state 1", "no", "0")
            every { stateManagerUi.showAllStates() } returns Unit
            every { stateManagerUi.addState() } returns Unit

            every { manageProjectUseCase.addProject(any<Project>()) } returns Result.success(true)

            // When
            projectManagerUi.launchUi()

            // Then
            verify { stateManagerUi.addState() }
            verify { outputPrinter.showMessage("Project added successfully") }
        }

        @Test
        fun `should prompt to add tasks when requested`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("3", "New Project", "state 1", "yes", "0")
            every { stateManagerUi.showAllStates() } returns Unit
            every { manageProjectUseCase.addProject(any()) } returns Result.success(true)
            every { taskManagerUi.createTask() } returns Unit

            // When
            projectManagerUi.launchUi()

            // Then
            verify { taskManagerUi.createTask() }
        }

        @Test
        fun `should handle project creation failure`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("3", "New Project", "1", "no", "0")
            every { stateManagerUi.showAllStates() } returns Unit
            every { manageProjectUseCase.addProject(any<Project>()) } returns Result.failure(Exception("Creation failed"))

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Creation failed") }
        }
    }

    @Nested
    inner class EditProject {
        @Test
        fun `should successfully edit existing project`() {
            // Given
            val project = buildProject(id = "1", name = "Old Name")
            val updatedProject = buildProject(id = "1", name = "New Name", stateId = project.stateId)
            every { manageProjectUseCase.getProjectById("1") } returns Result.success(project)
            every { inputReader.readStringOrNull() } returnsMany listOf("4", "1", "New Name", "", "no", "0")
            every { manageProjectUseCase.updateProject(updatedProject) } returns Result.success(true)

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("Project updated successfully") }
        }

        @Test
        fun `should show error when editing non-existent project`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("4", "999", "0")
            every { manageProjectUseCase.getProjectById("999") } returns Result.failure(NoSuchElementException())

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Project not found") }
        }

        @Test
        fun `should show error when edit operation fails`() {
            // Given
            val project = buildProject(id = "1")
            val updatedProject = buildProject(id = "1", name = "New Name")
            every { manageProjectUseCase.getProjectById("1") } returns Result.success(project)
            every { inputReader.readStringOrNull() } returnsMany listOf("4", "1", "New Name", "", "no", "0")
            every { manageProjectUseCase.updateProject(updatedProject) } returns Result.failure(Exception("Update failed"))

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Update failed") }
        }
    }

    @Nested
    inner class DeleteProject {
        @Test
        fun `should successfully delete existing project`() {
            // Given
            every { manageProjectUseCase.removeProjectById("1") } returns Result.success(true)
            every { inputReader.readStringOrNull() } returnsMany listOf("5", "1", "0")

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("Project deleted successfully") }
        }

        @Test
        fun `should show error when delete operation fails`() {
            // Given
            every { manageProjectUseCase.removeProjectById("1") } returns Result.failure(Exception("Deletion failed"))
            every { inputReader.readStringOrNull() } returnsMany listOf("5", "1", "0")

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Deletion failed") }
        }
    }

    @Nested
    inner class AssignUsersToProject {
        @Test
        fun `should successfully assign user to project`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("6", "no", "user1", "1", "no", "done", "0")
            every { manageProjectUseCase.isProjectExists("1") } returns Result.success(true)
            every { manageUsersAssignedToProjectUseCase.assignUserToProject(any(), any()) } returns Result.success(true)

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("User assigned successfully") }
        }

        @Test
        fun `should prompt to add new user when requested`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("6", "yes", "done", "0")
            every { CreateUserUi.launchUi() } returns Unit

            // When
            projectManagerUi.launchUi()

            // Then
            verify { CreateUserUi.launchUi() }
        }

//        @Test
//        fun `should show error when user does not exist`() {
//            // Given
//            every { inputReader.readStringOrNull() } returnsMany listOf("6", "no", "nonexistent", "done" ,"0")
//
//            // When
//            projectManagerUi.launchUi()
//
//            // Then
//            verify { outputPrinter.showMessage("User does not exist") }
//        }

        @Test
        fun `should show error when project does not exist`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("6", "no", "user1", "999", "no", "done", "0")
//            every { manageAuthenticationUseCase.isUserExists("user1") } returns Result.success(true)
            every { manageProjectUseCase.isProjectExists("999") } returns Result.failure(Throwable())
            every { manageUsersAssignedToProjectUseCase.assignUserToProject("user1", "999") } returns Result.success(
                true
            )

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("Project does not exist") }
        }

        @Test
        fun `should show error when assignment fails`() {
            // Given
            every { inputReader.readStringOrNull() } returnsMany listOf("6", "no", "user1", "1", "no", "done", "0")
//            every { manageAuthenticationUseCase.isUserExists("user1") } returns Result.success(true)
            every { manageProjectUseCase.isProjectExists("1") } returns Result.success(true)
            every { manageUsersAssignedToProjectUseCase.assignUserToProject("user1", "1") } returns Result.failure(
                Exception("Assignment failed")
            )

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessage("error: Assignment failed") }
        }
    }
}