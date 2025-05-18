import io.mockk.*
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.ui.features.audit.AuditManagerUI
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditManagerUITest {

    private lateinit var useCase: GetAuditUseCase
    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var auditManagerUi: AuditManagerUI
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        useCase = mockk(relaxed = true)
        reader = mockk()
        printer = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)
        auditManagerUi = AuditManagerUI(
            useCase = useCase,
            reader = reader,
            printer = printer,
            loginUseCase = loginUseCase
        )

        user = User(
            userRole = UserRole.MATE,
            username = "user",
            hashedPassword = "hashedPassword"
        )
    }

    @Test
    fun `invoke should call displayAuditsByProjectName when option 1 is selected`() = runTest {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("1", "projectA", "n")
        coEvery { useCase.getAuditsForProjectByName("projectA") } returns emptyList()

        // When
        auditManagerUi.launchUi()

        // Then
        coVerify { useCase.getAuditsForProjectByName("projectA") }
    }

    @Test
    fun `invoke should call displayAuditsByTaskName when option 2 is selected`() = runTest {
        every { reader.readStringOrNull() } returnsMany listOf("2", "taskA", "n")
        coEvery { useCase.getAuditsForTaskByName("taskA") } returns emptyList()

        auditManagerUi.launchUi()

        coVerify { useCase.getAuditsForTaskByName("taskA") }
    }

    @Test
    fun `invoke should call displayAllAudits when option 3 is selected`() = runTest {
        every { reader.readStringOrNull() } returnsMany listOf("3", "n")
        coEvery { useCase.getAuditsForUserById(user.id) } returns emptyList()

        auditManagerUi.launchUi()

        coVerify { useCase.getAuditsForUserById(user.id) }
    }

    @Test
    fun `invoke should print exiting message when option 4 is selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("4", "n")

        auditManagerUi.launchUi()

        verify { printer.showMessageLine(UiMessages.EXITING) }
    }

    @Test
    fun `invoke should handle invalid menu input`() {
        every { reader.readStringOrNull() } returnsMany listOf("abc", "n")

        auditManagerUi.launchUi()

        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `invoke should handle invalid project name when selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("1", null, "n")

        auditManagerUi.launchUi()

        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `invoke should handle invalid task name when selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("2", null, "n")

        auditManagerUi.launchUi()

        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `askSearchAgain should return true when Y is entered`() = runTest {
        every { reader.readStringOrNull() } returnsMany listOf("1", "proj", "y", "4", "n")
        coEvery { useCase.getAuditsForProjectByName("proj") } returns emptyList()

        auditManagerUi.launchUi()

        verify(exactly = 2) { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) }
    }

    @Test
    fun `askSearchAgain should return null when input is blank or n`() = runTest {
        every { reader.readStringOrNull() } returnsMany listOf("1", "proj", " ", "4", "n")
        coEvery { useCase.getAuditsForProjectByName("proj") } returns emptyList()

        auditManagerUi.launchUi()

        verify { printer.showMessageLine(UiMessages.EXITING) }
    }

    @Test
    fun `getMainMenuOption should return 0 on invalid input`() {
        every { reader.readStringOrNull() } returnsMany listOf("", "n")

        auditManagerUi.launchUi()

        verify { printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `displayAuditsByProjectName should show error message on failure`() = runTest {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("1", "projectFail", "n")
        coEvery { useCase.getAuditsForProjectByName("projectFail") } throws Exception("project error")

        // When
        auditManagerUi.launchUi()

        // Then
        verify { printer.showMessageLine("project error") }
    }

    @Test
    fun `displayAuditsByTaskName should show error message on failure`() = runTest {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("2", "taskFail", "n")
        coEvery { useCase.getAuditsForTaskByName("taskFail") } throws Exception("task error")

        // When
        auditManagerUi.launchUi()

        // Then
        verify { printer.showMessageLine("task error") }
    }

    @Test
    fun `askSearchAgain should handle uppercase and lowercase Y`() = runTest {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("1", "proj", "Y", "4", "n")
        coEvery { useCase.getAuditsForProjectByName("proj") } returns emptyList()

        // When
        auditManagerUi.launchUi()

        // Then
        verify(exactly = 2) { printer.showMessageLine(UiMessages.SHOW_AUDIT_OPTIONS) }
    }

    @Test
    fun `displayAllAudits should show error message on failure`() = runTest {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("3", "n")
        coEvery { useCase.getAuditsForUserById(user.id) } throws Exception("audit list error")

        // When
        auditManagerUi.launchUi()

        // Then
        verify { printer.showMessageLine("audit list error") }
    }


}
