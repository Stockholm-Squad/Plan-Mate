package logic.usecase.audit

import io.mockk.mockk
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ManageAuditSystemUseCaseTest{

  private lateinit var manageAuditSystemUseCase: ManageAuditSystemUseCase
  private lateinit var auditSystemRepository: AuditSystemRepository

  @BeforeEach
  fun setUp(){
   auditSystemRepository = mockk(relaxed = true)
   manageAuditSystemUseCase = ManageAuditSystemUseCase(auditSystemRepository)
  }


}