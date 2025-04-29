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

 @Test
 fun `getAuditSystemById should return correct audit system if exists`() {

  // Given
  val auditSystem = createAuditSystem(
   id = "1",
   auditSystemType = AuditSystemType.TASK,
   entityId = "123",
   changeDescription = "change description",
   changedBy = "Hamsa")

  auditSystemCsvDataSource.addAuditSystem(auditSystem)

  // When
  val result = auditSystemCsvDataSource.getAuditSystemById("1")

  // Then
  assertThat(result.isSuccess).isTrue()
  assertThat(result.getOrNull()?.id).isEqualTo("1")
 }

 @Test
 fun `getAllAuditSystems should return all added audit systems`() {

  // Given
  val auditSystem = listOf(createAuditSystem(
   id = "1",
   auditSystemType = AuditSystemType.TASK,
   entityId = "123",
   changeDescription = "change description",
   changedBy = "Hamsa"),
   createAuditSystem(
    id = "2",
    auditSystemType = AuditSystemType.TASK,
    entityId = "123",
    changeDescription = "change description",
    changedBy = "Hamsa"))

  auditSystem.forEach { auditSystemCsvDataSource.addAuditSystem(it) }

  // When
  val result = auditSystemCsvDataSource.getAllAuditSystems()

  // Then
  assertThat(result.isSuccess).isTrue()
  assertThat(result.getOrNull()).hasSize(2)
 }

 @Test
 fun `getAllAuditSystemsByType should return only matching type`() {

  // Given
  val auditSystem = listOf(createAuditSystem(
   id = "1",
   auditSystemType = AuditSystemType.TASK,
   entityId = "123",
   changeDescription = "change description",
   changedBy = "Hamsa"),
   createAuditSystem(
    id = "2",
    auditSystemType = AuditSystemType.TASK,
    entityId = "123",
    changeDescription = "change description",
    changedBy = "Hamsa"))
  auditSystem.forEach { auditSystemCsvDataSource.addAuditSystem(it) }

  // When
  val result = auditSystemCsvDataSource.getAllAuditSystemsByType(AuditSystemType.TASK)

  // Then
  assertThat(result.isSuccess).isTrue()
  assertThat(result.getOrNull()).hasSize(2)
  assertThat(result.getOrNull()?.first()?.auditSystemType).isEqualTo(AuditSystemType.TASK)
 }

 @Test
 fun `getAllAuditSystemsEntityId should return only matching entity id`() {
  // Given
  val auditSystem = listOf(createAuditSystem(
   id = "1",
   auditSystemType = AuditSystemType.TASK,
   entityId = "123",
   changeDescription = "change description",
   changedBy = "Hamsa"),
   createAuditSystem(
    id = "2",
    auditSystemType = AuditSystemType.TASK,
    entityId = "123",
    changeDescription = "change description",
    changedBy = "Hamsa"))
  auditSystem.forEach { auditSystemCsvDataSource.addAuditSystem(it) }

  // When
  val result = auditSystemCsvDataSource.getAllAuditSystemsEntityId("1")

  // Then
  assertThat(result.isSuccess).isTrue()
  assertThat(result.getOrNull()).hasSize(1)
  assertThat(result.getOrNull()?.first()?.entityId).isEqualTo("1")
 }
 }