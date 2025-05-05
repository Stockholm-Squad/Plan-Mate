import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.models.entities.User
import logic.models.entities.UserRole
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.UiMessages
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemManagerUiImpTest {

    private lateinit var useCase: ManageAuditSystemUseCase
    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var auditSystemUi: AuditSystemManagerUiImp
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        useCase = mockk(relaxed = true)
        reader = mockk()
        printer = mockk(relaxed = true)

        auditSystemUi = AuditSystemManagerUiImp(
            useCase,
            reader = reader,
            printer = printer
        )

        user = User(
            userRole = UserRole.MATE,
            username = "user",
            hashedPassword = "hashedPassword"
        )
    }

    @Test
    fun `invoke should call displayAuditsByProjectName when option 1 is selected`() {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("1", "projectA", "n")
        every { useCase.getProjectAuditsByName("projectA") } returns Result.success(emptyList())

        // When
        auditSystemUi.invoke(user)

        // Then
        verify { useCase.getProjectAuditsByName("projectA") }
    }

    @Test
    fun `invoke should call displayAuditsByTaskName when option 2 is selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("2", "taskA", "n")
        every { useCase.getTaskAuditsByName("taskA") } returns Result.success(emptyList())

        auditSystemUi.invoke(user)

        verify { useCase.getTaskAuditsByName("taskA") }
    }

    @Test
    fun `invoke should call displayAllAudits when option 3 is selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("3", "n")
        every { useCase.getAuditsByUserId(user.id) } returns Result.success(emptyList())

        auditSystemUi.invoke(user)

        verify { useCase.getAuditsByUserId(user.id) }
    }

    @Test
    fun `invoke should print exiting message when option 4 is selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("4", "n")

        auditSystemUi.invoke(user)

        verify { printer.showMessage(UiMessages.EXITING) }
    }

    @Test
    fun `invoke should handle invalid menu input`() {
        every { reader.readStringOrNull() } returnsMany listOf("abc", "n")

        auditSystemUi.invoke(user)

        verify { printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `invoke should handle invalid project name when selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("1", null, "n")

        auditSystemUi.invoke(user)

        verify { printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `invoke should handle invalid task name when selected`() {
        every { reader.readStringOrNull() } returnsMany listOf("2", null, "n")

        auditSystemUi.invoke(user)

        verify { printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `askSearchAgain should return true when Y is entered`() {
        every { reader.readStringOrNull() } returnsMany listOf("1", "proj", "y", "4", "n")
        every { useCase.getProjectAuditsByName("proj") } returns Result.success(emptyList())

        auditSystemUi.invoke(user)

        verify(exactly = 2) { printer.showMessage(UiMessages.SHOW_AUDIT_SYSTEM_OPTIONS) }
    }

    @Test
    fun `askSearchAgain should return null when input is blank or n`() {
        every { reader.readStringOrNull() } returnsMany listOf("1", "proj", " ", "4", "n")
        every { useCase.getProjectAuditsByName("proj") } returns Result.success(emptyList())

        auditSystemUi.invoke(user)

        verify { printer.showMessage(UiMessages.EXITING) }
    }

    @Test
    fun `getMainMenuOption should return 0 on invalid input`() {
        every { reader.readStringOrNull() } returnsMany listOf("", "n")

        auditSystemUi.invoke(user)

        verify { printer.showMessage(UiMessages.INVALID_SELECTION_MESSAGE) }
    }
    
    @Test
    fun `displayAuditsByProjectName should show error message on failure`() {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("1", "projectFail", "n")
        every { useCase.getProjectAuditsByName("projectFail") } returns Result.failure(Exception("project error"))

        // When
        auditSystemUi.invoke(user)

        // Then
        verify { printer.showMessage("project error") }
    }

    @Test
    fun `displayAuditsByTaskName should show error message on failure`() {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("2", "taskFail", "n")
        every { useCase.getTaskAuditsByName("taskFail") } returns Result.failure(Exception("task error"))

        // When
        auditSystemUi.invoke(user)

        // Then
        verify { printer.showMessage("task error") }
    }

    @Test
    fun `askSearchAgain should handle uppercase and lowercase Y`() {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("1", "proj", "Y", "4", "n")
        every { useCase.getProjectAuditsByName("proj") } returns Result.success(emptyList())

        // When
        auditSystemUi.invoke(user)

        // Then
        verify(exactly = 2) { printer.showMessage(UiMessages.SHOW_AUDIT_SYSTEM_OPTIONS) }
    }

    @Test
    fun `displayAllAudits should show error message on failure`() {
        // Given
        every { reader.readStringOrNull() } returnsMany listOf("3", "n")
        every { useCase.getAuditsByUserId(user.id) } returns Result.failure(Exception("audit list error"))

        // When
        auditSystemUi.invoke(user)

        // Then
        verify { printer.showMessage("audit list error") }
    }


}
