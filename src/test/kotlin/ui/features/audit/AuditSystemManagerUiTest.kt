package ui.features.audit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.data.repo.AuditSystemRepositoryImp
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.utils.SearchUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemManagerUiTest{

  private lateinit var auditSystemUseCase: ManageAuditSystemUseCase
  private lateinit var auditSystemRepositoryImp: AuditSystemRepositoryImp
  private lateinit var reader: InputReader
  private lateinit var printer: OutputPrinter
  private lateinit var auditSystemUi: AuditSystemManagerUi
  private lateinit var searchUtils: SearchUtils

  @BeforeEach
  fun setup() {
   auditSystemUseCase = mockk(relaxed = true)
   reader = mockk()
   printer = mockk(relaxed = true)
   searchUtils = mockk(relaxed = true)

   auditSystemUi = AuditSystemManagerUi(
       auditSystemUseCase,
       reader = reader,
       printer = printer,
       searchUtils = searchUtils,
       auditSystemRepositoryImp = auditSystemRepositoryImp
   )
  }


  @Test
  fun `showAuditSystemManagerUI should display all audit systems when option 1 is selected`() {
   // Given
   every { searchUtils.getMainMenuOption() } returns 1
   every { searchUtils.shouldSearchAgain(reader) } returns false

   // When
   auditSystemUi.showAuditSystemManagerUI()

   // Then
   verify(exactly = 1) { searchUtils.getMainMenuOption() }
   verify(exactly = 1) { searchUtils.shouldSearchAgain(reader) }
   verify(exactly = 1) { printer.showMessage(any()) }
  }



 }