package ui.features.audit

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.Audit
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.ui.features.audit.AuditManagerUI
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class AuditManagerUITest {
    private lateinit var auditManagerUI: AuditManagerUI
    private lateinit var useCase: GetAuditUseCase
    private lateinit var printer: OutputPrinter
    private lateinit var reader: InputReader
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setup() {
        useCase = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)
        auditManagerUI = AuditManagerUI(useCase, printer, reader, loginUseCase)
    }

    @Test
    fun `launchUi should exit immediately when no user is logged in`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null

        // When
        auditManagerUI.launchUi()

        // Then
        verify(exactly = 0) { printer.showMessageLine(any()) }
        verify(exactly = 0) { reader.readStringOrNull() }
    }

    @Test
    fun `launchUi should display menu and exit when user selects option 0`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns UUID.randomUUID()
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("0", null)
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) }
        verify { printer.showMessage(UiMessages.SELECT_OPTION) }
        verify { printer.showMessageLine(UiMessages.EXITING) }
    }

    @Test
    fun `launchUi should trigger displayAuditsByProjectName when user selects option 1 with valid project name`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("1", "projectX", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.PROMPT_PROJECT_NAME) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        val audits = listOf(mockk<Audit>())
        coEvery { useCase.getAuditsForProjectByName("projectX") } returns audits

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessage(UiMessages.PROMPT_PROJECT_NAME) }
        verify { printer.showAudits(audits, "testUser") }
    }

    @Test
    fun `launchUi should show invalid message when user selects option 1 with null project name`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("1", null, null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.PROMPT_PROJECT_NAME) } returns Unit
        every { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `launchUi should handle exception when user selects option 1 and useCase fails`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("1", "projectX", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.PROMPT_PROJECT_NAME) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        coEvery { useCase.getAuditsForProjectByName("projectX") } throws Exception("Error fetching audits")
        every { printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_AUDITS} Error fetching audits") } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_AUDITS} Error fetching audits") }
    }

    @Test
    fun `launchUi should trigger displayAuditsByTaskName when user selects option 2 with valid task name`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("2", "taskY", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.PROMPT_TASK_NAME) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        val audits = listOf(mockk<Audit>())
        coEvery { useCase.getAuditsForTaskByName("taskY") } returns audits

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessage(UiMessages.PROMPT_TASK_NAME) }
        verify { printer.showAudits(audits, "testUser") }
    }

    @Test
    fun `launchUi should show invalid message when user selects option 2 with null task name`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("2", null, null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.PROMPT_TASK_NAME) } returns Unit
        every { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `launchUi should handle exception when user selects option 2 and useCase fails`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("2", "taskY", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.PROMPT_TASK_NAME) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        coEvery { useCase.getAuditsForTaskByName("taskY") } throws Exception("Task error")
        every { printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_AUDITS} Task error") } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_AUDITS} Task error") }
    }

    @Test
    fun `launchUi should trigger displayAllAudits when user selects option 3`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("3", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        val audits = listOf(mockk<Audit>())
        coEvery { useCase.getAuditsForUserById(userId) } returns audits

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showAudits(audits, "testUser") }
    }

    @Test
    fun `launchUi should handle exception when user selects option 3 and useCase fails`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("3", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        coEvery { useCase.getAuditsForUserById(userId) } throws Exception("User audits error")
        every { printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_AUDITS} User audits error") } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine("${UiMessages.FAILED_TO_LOAD_AUDITS} User audits error") }
    }

    @Test
    fun `launchUi should continue loop when user selects to search again`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("3", "y", "0", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        val audits = listOf(mockk<Audit>())
        coEvery { useCase.getAuditsForUserById(userId) } returns audits

        // When
        auditManagerUI.launchUi()

        // Then
        verify(exactly = 2) { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) }
        verify { printer.showAudits(audits, "testUser") }
        verify { printer.showMessageLine(UiMessages.EXITING) }
    }

    @Test
    fun `launchUi should show invalid message when user selects invalid numeric option`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("4", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit

        // When
        auditManagerUI.launchUi()

        // Then
        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `launchUi should exit loop when user enters non-y input for search again prompt`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("3", "n", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        val audits = listOf(mockk<Audit>())
        coEvery { useCase.getAuditsForUserById(userId) } returns audits

        // When
        auditManagerUI.launchUi()

        // Then
        verify(exactly = 1) { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) }
        verify { printer.showAudits(audits, "testUser") }
        verify { printer.showMessageLine(UiMessages.EXITING) }
    }

    @Test
    fun `launchUi should exit loop when user enters whitespace-only input for search again prompt`() {
        // Given
        val userId = UUID.randomUUID()
        every { loginUseCase.getCurrentUser() } returns mockk {
            every { id } returns userId
            every { username } returns "testUser"
        }
        every { reader.readStringOrNull() } returnsMany listOf("3", "  ", null)
        every { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) } returns Unit
        every { printer.showMessage(UiMessages.SELECT_OPTION) } returns Unit
        every { printer.showMessage(UiMessages.SEARCH_AGAIN_PROMPT) } returns Unit
        every { printer.showMessageLine(UiMessages.EXITING) } returns Unit
        val audits = listOf(mockk<Audit>())
        coEvery { useCase.getAuditsForUserById(userId) } returns audits

        // When
        auditManagerUI.launchUi()

        // Then
        verify(exactly = 1) { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) }
        verify { printer.showAudits(audits, "testUser") }
        verify { printer.showMessageLine(UiMessages.EXITING) }
    }

}