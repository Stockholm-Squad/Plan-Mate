package data.datasources.audit

import logic.model.entities.AuditSystemType
import com.google.common.truth.Truth.assertThat
import org.example.data.datasources.audit.AuditSystemCsvDataSource
import org.example.utils.createAuditSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemCsvDataSourceTest{

  private lateinit var auditSystemCsvDataSource: AuditSystemCsvDataSource


  @BeforeEach
  fun setUp() {
   auditSystemCsvDataSource = AuditSystemCsvDataSource()
  }

 @Test
 fun `addAuditSystem should return success when adding is valid`() {

  // Given
  val auditSystem = createAuditSystem(
   id = "1",
   auditSystemType = AuditSystemType.TASK,
   entityId = "123",
   changeDescription = "change description",
   changedBy = "Hamsa")


  // When
  val result = auditSystemCsvDataSource.addAuditSystem(auditSystem)

  // Then
  assertThat(result.isSuccess).isTrue()
  assertThat(result.getOrNull()).isTrue()
 }
 }