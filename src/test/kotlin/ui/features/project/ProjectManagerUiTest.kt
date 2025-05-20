package ui.features.project

import io.mockk.*
import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.EntityType
import org.example.logic.entities.Project
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ProjectManagerUiTest {
    private lateinit var inputReader: InputReader
    private lateinit var outputPrinter: OutputPrinter
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var adminStateManagerUi: AdminEntityStateManagerUi
    private lateinit var showAllEntityStateManagerUi: ShowAllEntityStateManagerUi
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase
    private lateinit var auditServicesUseCase: AuditServicesUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var taskManagerUi: TaskManagerUi
    private lateinit var projectManagerUi: ProjectManagerUi

    @BeforeEach
    fun setUp() {
        inputReader = mockk(relaxed = true)
        outputPrinter = mockk(relaxed = true)
        manageProjectUseCase = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        adminStateManagerUi = mockk(relaxed = true)
        showAllEntityStateManagerUi = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)
        auditServicesUseCase = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)
        taskManagerUi = mockk(relaxed = true)

        projectManagerUi = ProjectManagerUi(
            inputReader,
            outputPrinter,
            manageProjectUseCase,
            getProjectsUseCase,
            adminStateManagerUi,
            showAllEntityStateManagerUi,
            manageStatesUseCase,
            auditServicesUseCase,
            loginUseCase,
            taskManagerUi
        )
    }

    @Nested
    inner class LaunchUiTests {

        @Test
        fun `test launchUi with invalid user`() {
            // Given
            every { loginUseCase.getCurrentUser() } returns null
            every { outputPrinter.showMessageLine(UiMessages.INVALID_USER) } just Runs

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.INVALID_USER) }
        }

        @Test
        fun `test launchUi with valid user and showAllProjects selection`() {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "1" andThen "0"
            coEvery { getProjectsUseCase.getAllProjects() } returns emptyList()

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.NO_PROJECTS_FOUND) }
        }

        @Test
        fun `test launchUi with valid user and showProjectByName selection`() {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            val projectName = "TestProject"
            val stateId = UUID.randomUUID()
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, stateId)
            every { inputReader.readStringOrNull() } returns "2" andThen projectName andThen "0"
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(stateId) } returns stateName


            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine("${UiMessages.NAME} $projectName") }
        }

        @Test
        fun `test launchUi with valid user and addProject selection`() = runBlocking {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            val projectName = "TestProject"
            val stateName = "Active"

            every { inputReader.readStringOrNull() } returns "3" andThen projectName andThen stateName andThen "n" andThen "0"
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { manageProjectUseCase.addProject(projectName, stateName) } returns true
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk()
            coEvery { auditServicesUseCase.addAuditForAddEntity(any(), any(), any(), any()) } just Runs
            every { taskManagerUi.addTask(projectName) } just Runs

            // When
            projectManagerUi.launchUi()

            // Then
            coVerify { manageProjectUseCase.addProject(projectName, stateName) }
        }

        @Test
        fun `test launchUi with valid user and updateProject selection`() = runBlocking {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs

            val projectName = "TestProject"
            val newProjectName = "UpdatedProject"
            val stateName = "Active"
            val newStateName = "Inactive"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns "4" andThen projectName andThen newProjectName andThen newStateName andThen "0"
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(any()) } returns stateName
            coEvery { manageProjectUseCase.updateProject(any(), any(), any()) } returns true
            coEvery { auditServicesUseCase.addAuditForUpdateEntity(any(), any(), any(), any(), any()) } just Runs

            // When
            projectManagerUi.launchUi()

            // Then
            coVerify { manageProjectUseCase.updateProject(project.id, newProjectName, newStateName) }
        }

        @Test
        fun `test launchUi with valid user and deleteProject selection`() = runBlocking {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs

            val projectName = "TestProject"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns "5" andThen projectName andThen "0"
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageProjectUseCase.removeProjectByName(projectName) } returns true
            coEvery { auditServicesUseCase.addAuditForDeleteEntity(any(), any(), any()) } just Runs

            // When
            projectManagerUi.launchUi()

            // Then
            coVerify { manageProjectUseCase.removeProjectByName(projectName) }
        }

        @Test
        fun `test launchUi with valid user and exit selection`() {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "0"

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.SHOW_PROJECT_MANAGEMENT_OPTIONS) }
        }

        @Test
        fun `test launchUi with valid user and invalid selection`() {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "invalid" andThen "0"

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
        }

    }

    @Nested
    inner class showAllProjectsTests {

        @Test
        fun `test showAllProjects when list is empty`() = runBlocking {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            coEvery { getProjectsUseCase.getAllProjects() } returns emptyList()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "1" andThen "0"

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.NO_PROJECTS_FOUND) }
            coVerify { getProjectsUseCase.getAllProjects() }
        }

        @Test
        fun `test showAllProjects with projects`() = runBlocking {
            // Given
            val projects = listOf(
                Project(UUID.randomUUID(), "Project1", UUID.randomUUID()),
                Project(UUID.randomUUID(), "Project2", UUID.randomUUID())
            )
            every { loginUseCase.getCurrentUser() } returns mockk()
            coEvery { getProjectsUseCase.getAllProjects() } returns projects
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "1" andThen "0"

            // When
            projectManagerUi.launchUi()

            // Then
            verify {
                outputPrinter.showMessageLine("1. Project1")
                outputPrinter.showMessageLine("2. Project2")
            }
            coVerify { getProjectsUseCase.getAllProjects() }
        }

        @Test
        fun `test showAllProjects when exception occurs`() = runBlocking {
            // Given
            val errorMessage = "Failed to fetch projects"
            every { loginUseCase.getCurrentUser() } returns mockk()
            coEvery { getProjectsUseCase.getAllProjects() } throws Exception(errorMessage)
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "1" andThen "0"

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(errorMessage) }
            coVerify { getProjectsUseCase.getAllProjects() }
        }

        @Test
        fun `test showAllProjects when exception occurs without message`() = runBlocking {
            // Given
            every { loginUseCase.getCurrentUser() } returns mockk()
            coEvery { getProjectsUseCase.getAllProjects() } throws Exception()
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { outputPrinter.showMessage(any()) } just Runs
            every { inputReader.readStringOrNull() } returns "1" andThen "0"

            // When
            projectManagerUi.launchUi()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.UNKNOWN_ERROR) }
            coVerify { getProjectsUseCase.getAllProjects() }
        }
    }


    @Nested
    inner class showProjectByNameTests {

        @Test
        fun `test showProjectByName successful`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateId = UUID.randomUUID()
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, stateId)

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(stateId) } returns stateName

            // When
            projectManagerUi.showProjectByName()

            // Then
            verify {
                outputPrinter.showMessageLine(UiMessages.PROJECT_DETAILS)
                outputPrinter.showMessageLine("${UiMessages.NAME} $projectName")
                outputPrinter.showMessageLine("${UiMessages.STATE} $stateName")
            }
        }

        @Test
        fun `test showProjectByName with empty input retry`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateId = UUID.randomUUID()
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, stateId)

            every { inputReader.readStringOrNull() } returns "" andThen projectName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(stateId) } returns stateName

            // When
            projectManagerUi.showProjectByName()

            // Then
            verify {
                outputPrinter.showMessageLine("${UiMessages.PROJECT_CAN_NOT_BE_EMPTY} ${UiMessages.PLEASE_TRY_AGAIN}")
                outputPrinter.showMessageLine(UiMessages.PROJECT_DETAILS)
            }
        }

        @Test
        fun `test showProjectByName when project not found`() = runBlocking {
            // Given
            val projectName = "NonExistentProject"
            val exception = Exception("Project not found")

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws exception

            // When
            projectManagerUi.showProjectByName()

            // Then
            verify { outputPrinter.showMessageLine("Project not found") }
        }

        @Test
        fun `test showProjectByName with null input retry`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateId = UUID.randomUUID()
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, stateId)

            every { inputReader.readStringOrNull() } returns null andThen projectName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(stateId) } returns stateName

            // When
            projectManagerUi.showProjectByName()

            // Then
            verify {
                outputPrinter.showMessageLine("${UiMessages.PROJECT_CAN_NOT_BE_EMPTY} ${UiMessages.PLEASE_TRY_AGAIN}")
                outputPrinter.showMessageLine(UiMessages.PROJECT_DETAILS)
            }
        }

        @Test
        fun `test showProjectByName when exception occurs with null message`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val exception = Exception()

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws exception

            // When
            projectManagerUi.showProjectByName()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.UNKNOWN_ERROR) }
        }
    }

    @Nested
    inner class addProjectTests {

        @Test
        fun `test addProject successful with tasks`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName andThen stateName andThen "y" andThen "n"
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { manageProjectUseCase.addProject(projectName, stateName) } returns true
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { auditServicesUseCase.addAuditForAddEntity(any(), any(), any(), any()) } just Runs
            every { taskManagerUi.addTask(projectName) } just Runs

            // When
            projectManagerUi.addProject()

            // Then
            coVerify {
                manageProjectUseCase.addProject(projectName, stateName)
                auditServicesUseCase.addAuditForAddEntity(
                    entityType = EntityType.PROJECT,
                    entityName = projectName,
                    entityId = project.id,
                    additionalInfo = stateName
                )
                taskManagerUi.addTask(projectName)
            }
        }

        @Test
        fun `test addProject with blank input`() = runBlocking {
            // Given
            every { inputReader.readStringOrNull() } returns null
            every { outputPrinter.showMessage(any()) } just Runs

            // When
            projectManagerUi.addProject()

            // Then
            verify {
                outputPrinter.showMessage("${UiMessages.PROJECT_NAME_PROMPT} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}")
            }
            coVerify(exactly = 0) { manageProjectUseCase.addProject(any(), any()) }
        }

        @Test
        fun `test addProject failure`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateName = "Active"

            every { inputReader.readStringOrNull() } returns projectName andThen stateName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { manageProjectUseCase.addProject(projectName, stateName) } returns false

            // When
            projectManagerUi.addProject()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.FAILED_TO_ADD_PROJECT) }
        }

        @Test
        fun `test addProject with new state creation`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateName = "stateName"

            every { inputReader.readStringOrNull() } returns projectName andThen "new" andThen stateName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            every { adminStateManagerUi.addState() } just Runs

            // When
            projectManagerUi.addProject()

            // Then
            verify { adminStateManagerUi.addState() }
        }

    }

    @Nested
    inner class updateProjectTests {

        @Test
        fun `test updateProject successful`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val newProjectName = "UpdatedProject"
            val stateName = "Active"
            val newStateName = "Inactive"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName andThen newProjectName andThen newStateName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(any()) } returns stateName
            coEvery { manageProjectUseCase.updateProject(any(), any(), any()) } returns true
            coEvery { auditServicesUseCase.addAuditForUpdateEntity(any(), any(), any(), any(), any()) } just Runs

            // When
            projectManagerUi.updateProject()

            // Then
            coVerify {
                manageProjectUseCase.updateProject(project.id, newProjectName, newStateName)
                auditServicesUseCase.addAuditForUpdateEntity(
                    entityType = EntityType.PROJECT,
                    existEntityName = projectName,
                    newEntityName = newProjectName,
                    entityId = project.id,
                    newStateName = newStateName
                )
            }
        }

        @Test
        fun `test updateProject with blank input`() = runBlocking {
            // Given
            every { inputReader.readStringOrNull() } returns null
            every { outputPrinter.showMessage(any()) } just Runs

            // When
            projectManagerUi.updateProject()

            // Then
            coVerify(exactly = 0) { manageProjectUseCase.updateProject(any(), any(), any()) }
        }

        @Test
        fun `test updateProject with new state only`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateName = "Active"
            val newStateName = "Inactive"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName andThen projectName andThen newStateName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(any()) } returns stateName
            coEvery { manageProjectUseCase.updateProject(any(), any(), any()) } returns true
            coEvery { auditServicesUseCase.addAuditForUpdateEntity(any(), any(), any(), any(), any()) } just Runs

            // When
            projectManagerUi.updateProject()

            // Then
            coVerify {
                manageProjectUseCase.updateProject(project.id, projectName, newStateName)
                auditServicesUseCase.addAuditForUpdateEntity(
                    entityType = EntityType.PROJECT,
                    existEntityName = projectName,
                    newEntityName = projectName,
                    entityId = project.id,
                    newStateName = newStateName
                )
            }
        }

        @Test
        fun `test updateProject when both name and state are blank`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName andThen null andThen null
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(any()) } returns stateName

            // When
            projectManagerUi.updateProject()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.PROJECT_DOES_NOT_CHANGED) }
            coVerify(exactly = 0) { manageProjectUseCase.updateProject(any(), any(), any()) }
        }

        @Test
        fun `test updateProject when update operation fails`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val newProjectName = "UpdatedProject"
            val stateName = "Active"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName andThen newProjectName andThen stateName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            every { showAllEntityStateManagerUi.launchUi() } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageStatesUseCase.getEntityStateNameByStateId(any()) } returns stateName
            coEvery { manageProjectUseCase.updateProject(any(), any(), any()) } returns false

            // When
            projectManagerUi.updateProject()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.FAILED_TO_UPDATE_PROJECT) }
            coVerify(exactly = 0) { auditServicesUseCase.addAuditForUpdateEntity(any(), any(), any(), any(), any()) }
        }

        @Test
        fun `test updateProject when project not found`() = runBlocking {
            // Given
            val projectName = "NonExistentProject"
            val errorMessage = "Project not found"

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws Exception(errorMessage)

            // When
            projectManagerUi.updateProject()

            // Then
            verify { outputPrinter.showMessageLine("${UiMessages.FAILED_TO_UPDATE_PROJECT} $errorMessage") }
        }
    }


    @Nested
    inner class deleteProjectTests {

        @Test
        fun `test deleteProject successful`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageProjectUseCase.removeProjectByName(projectName) } returns true
            coEvery { auditServicesUseCase.addAuditForDeleteEntity(any(), any(), any()) } just Runs

            // When
            projectManagerUi.deleteProject()

            // Then
            coVerify {
                manageProjectUseCase.removeProjectByName(projectName)
                auditServicesUseCase.addAuditForDeleteEntity(
                    entityType = EntityType.PROJECT,
                    entityName = projectName,
                    entityId = project.id
                )
            }
            verify { outputPrinter.showMessageLine(UiMessages.PROJECT_DELETED) }
        }

        @Test
        fun `test deleteProject when project not found`() = runBlocking {
            // Given
            val projectName = "NonExistentProject"
            val exception = Exception("Project not found")

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws exception

            // When
            projectManagerUi.deleteProject()

            // Then
            verify { outputPrinter.showMessageLine("${UiMessages.FAILED_TO_DELETE_PROJECT} Project not found") }
        }

        @Test
        fun `test deleteProject with blank input`() = runBlocking {
            // Given
            every { inputReader.readStringOrNull() } returns null
            every { outputPrinter.showMessage(any()) } just Runs
            every { outputPrinter.showMessageLine(any()) } just Runs

            // When
            projectManagerUi.deleteProject()

            // Then
            verify { outputPrinter.showMessageLine("${UiMessages.ENTER_PROJECT_NAME_TO_DELETE} ${UiMessages.OR_LEAVE_IT_BLANK_TO_BACK}") }
            coVerify(exactly = 0) { manageProjectUseCase.removeProjectByName(any()) }
        }

        @Test
        fun `test deleteProject when removal fails`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageProjectUseCase.removeProjectByName(projectName) } returns false

            // When
            projectManagerUi.deleteProject()

            // Then
            verify { outputPrinter.showMessageLine(UiMessages.FAILED_TO_DELETE_PROJECT) }
            coVerify(exactly = 0) { auditServicesUseCase.addAuditForDeleteEntity(any(), any(), any()) }
        }

        @Test
        fun `test deleteProject when audit throws exception`() = runBlocking {
            // Given
            val projectName = "TestProject"
            val project = Project(UUID.randomUUID(), projectName, UUID.randomUUID())
            val errorMessage = "Failed to add audit"

            every { inputReader.readStringOrNull() } returns projectName
            every { outputPrinter.showMessageLine(any()) } just Runs
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { manageProjectUseCase.removeProjectByName(projectName) } returns true
            coEvery { auditServicesUseCase.addAuditForDeleteEntity(any(), any(), any()) } throws Exception(errorMessage)

            // When
            projectManagerUi.deleteProject()

            // Then
            verify { outputPrinter.showMessageLine("${UiMessages.FAILED_TO_DELETE_PROJECT} $errorMessage") }
        }
    }
}