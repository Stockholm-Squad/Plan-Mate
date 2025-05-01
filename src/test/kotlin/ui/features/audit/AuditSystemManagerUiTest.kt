package ui.features.audit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.data.repo.AuditSystemRepositoryImp
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.utils.Constant
import org.example.utils.SearchUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemManagerUiTest {

    private lateinit var useCase: ManageAuditSystemUseCase
    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var auditSystemUi: AuditSystemManagerUi
    private lateinit var searchUtils: SearchUtils

    @BeforeEach
    fun setup() {
        useCase = mockk(relaxed = true)
        reader = mockk()
        printer = mockk(relaxed = true)
        searchUtils = mockk(relaxed = true)

        auditSystemUi = AuditSystemManagerUi(
            useCase,
            reader = reader,
            printer = printer,
            searchUtils = searchUtils,
        )
    }

    @Test
    fun `showAuditSystemManagerUI Should call method which user choose 1`() {
        // given
        every { searchUtils.getMainMenuOption() } returns 1 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { useCase.getAllAuditSystems() } returns Result.success(emptyList())
        // when
        auditSystemUi.showAuditSystemManagerUI()
        // then
        verify { useCase.getAllAuditSystems() }
    }

    @Test
    fun `showAuditSystemManagerUI Should call displayAuditLogsByTaskId when option is 2 with valid input`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 2 andThen 6
        every { reader.readStringOrNull() } returns "taskId"
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { useCase.getTaskChangeLogsById("taskId") } returns Result.success(emptyList())
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { useCase.getTaskChangeLogsById("taskId") }
    }

    @Test
    fun `showAuditSystemManagerUI Should handle null input when option is 2`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 2 andThen 6
        every { reader.readStringOrNull() } returns null
        every { searchUtils.shouldSearchAgain(reader) } returns false
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage(Constant.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `showAuditSystemManagerUI Should call displayAuditLogsByProjectId when option is 3 with valid input`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 3 andThen 6
        every { reader.readStringOrNull() } returns "projId"
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { useCase.getProjectChangeLogsById("projId") } returns Result.success(emptyList())
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { useCase.getProjectChangeLogsById("projId") }
    }

    @Test
    fun `showAuditSystemManagerUI Should handle null input when option is 3`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 3 andThen 6
        every { reader.readStringOrNull() } returns null
        every { searchUtils.shouldSearchAgain(reader) } returns false
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage(Constant.INVALID_SELECTION_MESSAGE) }
    }


    @Test
    fun `showAuditSystemManagerUI Should call displayAuditByAuditId when option is 4 with valid input`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 4 andThen 6
        every { reader.readStringOrNull() } returns "auditId"
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { useCase.getAuditSystemByID("auditId") } returns Result.success(emptyList())
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { useCase.getAuditSystemByID("auditId") }
    }

    @Test
    fun `showAuditSystemManagerUI Should handle null input when option is 4`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 4 andThen 6
        every { reader.readStringOrNull() } returns null
        every { searchUtils.shouldSearchAgain(reader) } returns false
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage(Constant.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `showAuditSystemManagerUI Should call displayAuditByUsername when option is 5 with valid input`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 5 andThen 6
        every { reader.readStringOrNull() } returns "user"
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { useCase.getUserChangeLogsByUsername("user") } returns Result.success(emptyList())
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { useCase.getUserChangeLogsByUsername("user") }
    }

    @Test
    fun `showAuditSystemManagerUI Should handle null input when option is 5`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 5 andThen 6
        every { reader.readStringOrNull() } returns null
        every { searchUtils.shouldSearchAgain(reader) } returns false
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage(Constant.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `showAuditSystemManagerUI Should print exit message when option is 6`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage(Constant.EXITING) }
    }

    @Test
    fun `showAuditSystemManagerUI Should print invalid message for unknown option`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 9 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage(Constant.INVALID_SELECTION_MESSAGE) }
    }

    @Test
    fun `displayAllAuditSystems should handle failure`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 1 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { useCase.getAllAuditSystems() } returns Result.failure(Exception("Load failed"))
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage("Load failed") }
    }

    @Test
    fun `displayAuditLogsByTaskId should handle failure`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 2 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { reader.readStringOrNull() } returns "123"
        every { useCase.getTaskChangeLogsById("123") } returns Result.failure(Exception("Task fetch failed"))
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage("Task fetch failed") }
    }

    @Test
    fun `displayAuditLogsByProjectId should handle failure`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 3 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { reader.readStringOrNull() } returns "456"
        every { useCase.getProjectChangeLogsById("456") } returns Result.failure(Exception("Project fetch failed"))
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage("Project fetch failed") }
    }

    @Test
    fun `displayAuditByAuditId should handle failure`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 4 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { reader.readStringOrNull() } returns "audit123"
        every { useCase.getAuditSystemByID("audit123") } returns Result.failure(Exception("Audit fetch failed"))
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage("Audit fetch failed") }
    }

    @Test
    fun `displayAuditByUsername should handle failure`() {
        //given
        every { searchUtils.getMainMenuOption() } returns 5 andThen 6
        every { searchUtils.shouldSearchAgain(reader) } returns false
        every { reader.readStringOrNull() } returns "userX"
        every { useCase.getUserChangeLogsByUsername("userX") } returns Result.failure(Exception("User fetch failed"))
        //when
        auditSystemUi.showAuditSystemManagerUI()
        //then
        verify { printer.showMessage("User fetch failed") }
    }

}