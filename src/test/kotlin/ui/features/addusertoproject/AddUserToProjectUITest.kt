package ui.features.addusertoproject

import io.mockk.*
import org.example.logic.entities.Project
import org.example.logic.entities.User
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.addusertoproject.AddUserToProjectUI
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class AddUserToProjectUITest {
    private lateinit var addUserToProjectUI: AddUserToProjectUI
    private lateinit var inputReader: InputReader
    private lateinit var outputPrinter: OutputPrinter
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase
    private lateinit var createUserUi: CreateUserUi

    @BeforeEach
    fun setup() {
        inputReader = mockk(relaxed = true)
        outputPrinter = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        manageUsersAssignedToProjectUseCase = mockk(relaxed = true)
        createUserUi = mockk(relaxed = true)
        addUserToProjectUI = AddUserToProjectUI(inputReader, outputPrinter, getProjectsUseCase, manageUsersAssignedToProjectUseCase, createUserUi)
    }

    @Test
    fun `launchUi should exit when user selects 0`() {
        // Given
        every { inputReader.readIntOrNull() } returns 0

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify(exactly = 1) { outputPrinter.showMessageLine(UiMessages.SHOW_ADD_USER_TO_PROJECT_OPTIONS) }
    }

    @Test
    fun `launchUi should assign users when user selects 1`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("n", "user1", "Project1", "done")
        val projectId = UUID.randomUUID()
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns Project(id = projectId, title = "Project1", stateId = UUID.randomUUID())
        coEvery { manageUsersAssignedToProjectUseCase.addUserToProject(projectId, "user1") } returns true

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(UiMessages.USER_ASSIGNED_TO_PROJECT) }
    }

    @Test
    fun `launchUi should remove user when user selects 3`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(3, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("Project1", "user1")
        val projectId = UUID.randomUUID()
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns Project(id = projectId, title = "Project1", stateId = UUID.randomUUID())
        coEvery { manageUsersAssignedToProjectUseCase.deleteUserFromProject("Project1", "user1") } returns true

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(UiMessages.USER_DELETED_FROM_PROJECT) }
    }

    @Test
    fun `launchUi should handle invalid input`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(5, 0)

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `assignUsersToProject should call create user ui when user inputs y`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("y", "user1", "Project1", "done")
        val projectId = UUID.randomUUID()
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns Project(id = projectId, title = "Project1", stateId = UUID.randomUUID())
        coEvery { manageUsersAssignedToProjectUseCase.addUserToProject(projectId, "user1") } returns true

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { createUserUi.launchUi() }
    }

    @Test
    fun `showUsersAssignedToProject should show no users if list is empty`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(2, 0)
        every { inputReader.readStringOrNull() } returns "EmptyProject"
        val projectId = UUID.randomUUID()
        coEvery { getProjectsUseCase.getProjectByName("EmptyProject") } returns Project(id = projectId, title = "Project1", stateId = UUID.randomUUID())
        coEvery { manageUsersAssignedToProjectUseCase.getUsersByProjectId(projectId) } returns emptyList()

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(UiMessages.NO_USERS_ASSIGNED_TO_PROJECT) }
    }

    @Test
    fun `assignUsersToProject should break when username is done`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("n", "done", "Project1")
        val project = Project(UUID.randomUUID(), "Project1", UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns project

        // When
        addUserToProjectUI.launchUi()

        // Then
        coVerify(exactly = 0) { manageUsersAssignedToProjectUseCase.addUserToProject(any(), any()) }
    }

    @Test
    fun `assignUsersToProject should skip adding if username is null`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("n", null, "done", "Project1")
        val project = Project(UUID.randomUUID(), "Project1", UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns project

        // When
        addUserToProjectUI.launchUi()

        // Then
        coVerify(exactly = 0) { manageUsersAssignedToProjectUseCase.addUserToProject(any(), any()) }
    }

    @Test
    fun `assignUsersToProject should show error message if addUserToProject returns false`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("n", "user1", "Project1", "done")
        val project = Project(UUID.randomUUID(), "Project1", UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns project
        coEvery { manageUsersAssignedToProjectUseCase.addUserToProject(project.id, "user1") } returns false

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(match { it.contains(UiMessages.FAILED_TO_ASSIGN_USER_TO_PROJECT) }) }
        verify { outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN) }
    }

    @Test
    fun `assignUsersToProject should catch exception when getting project fails`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("n", "user1", "Project1", "done")
        coEvery { getProjectsUseCase.getProjectByName("Project1") } throws RuntimeException("DB failure")

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(match { it.contains(UiMessages.FAILED_TO_ASSIGN_USER_TO_PROJECT) }) }
        verify { outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN) }
    }

    @Test
    fun `assignUsersToProject should catch exception when addUserToProject throws`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(1, 0)
        every { inputReader.readStringOrNull() } returnsMany listOf("n", "user1", "Project1", "done")
        val project = Project(UUID.randomUUID(), "Project1", UUID.randomUUID())
        coEvery { getProjectsUseCase.getProjectByName("Project1") } returns project
        coEvery { manageUsersAssignedToProjectUseCase.addUserToProject(any(), any()) } throws RuntimeException("Insert failed")

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(match { it.contains(UiMessages.FAILED_TO_ASSIGN_USER_TO_PROJECT) }) }
        verify { outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN) }
    }

    @Test
    fun `showUsersAssignedToProject should print assigned users`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(2, 0)
        val usernameList = listOf(User(UUID.randomUUID(), "user1", hashedPassword = "test"), User(UUID.randomUUID(), "user2", hashedPassword = "<PASSWORD>"))
        every { inputReader.readStringOrNull() } returns "ProjectX"
        val projectId = UUID.randomUUID()
        coEvery { getProjectsUseCase.getProjectByName("ProjectX") } returns Project(projectId, "ProjectX", UUID.randomUUID())
        coEvery { manageUsersAssignedToProjectUseCase.getUsersByProjectId(projectId) } returns usernameList

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine("${UiMessages.USERS_ASSIGNED_TO} ProjectX: ") }
        verify { outputPrinter.showMessageLine("1. user1") }
        verify { outputPrinter.showMessageLine("2. user2") }
    }

    @Test
    fun `showUsersAssignedToProject should catch exception when fetching users`() {
        // Given
        every { inputReader.readIntOrNull() } returnsMany listOf(2, 0)
        every { inputReader.readStringOrNull() } returns "ProjectFail"
        val projectId = UUID.randomUUID()
        coEvery { getProjectsUseCase.getProjectByName("ProjectFail") } returns Project(projectId, "ProjectFail", UUID.randomUUID())
        coEvery { manageUsersAssignedToProjectUseCase.getUsersByProjectId(projectId) } throws RuntimeException("Connection lost")

        // When
        addUserToProjectUI.launchUi()

        // Then
        verify { outputPrinter.showMessageLine(match { it.contains(UiMessages.FAILED_LOADING_USER_ASSIGNED_TO_PROJECT) }) }
        verify { outputPrinter.showMessageLine(UiMessages.PLEASE_TRY_AGAIN) }
    }
}
