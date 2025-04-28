package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType
import org.example.data.datasources.audit.AuditSystemDataSource
import org.example.data.repo.AuditSystemRepositoryImp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemRepositoryImpTest{

  private lateinit var auditSystemRepositoryImp: AuditSystemRepositoryImp
  private lateinit var auditSystemDataSource: AuditSystemDataSource

  @BeforeEach
  fun setUp() {
   auditSystemDataSource = mockk(relaxed = true)
   auditSystemRepositoryImp = AuditSystemRepositoryImp(auditSystemDataSource)
  }

 @Test
 fun `addAuditSystem should return true when successfully added audit`() {
  // Given
  val auditSystem = AuditSystem(
      id = "1",
      auditSystemType = AuditSystemType.TASK,
      entityId = "123",
      changeDescription = "change description",
      dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
      changedBy = "Hamsa"
  )

  every { auditSystemRepositoryImp.addAuditSystem(auditSystem) } returns Result.success(true)

  //When
  val result = auditSystemRepositoryImp.addAuditSystem(auditSystem)

  //Then
  assertThat(result).isNotNull()
  assertThat(result.isSuccess).isTrue()
  verify(exactly = 1) { auditSystemRepositoryImp.addAuditSystem(auditSystem) }
 }

 @Test
 fun `getAuditSystemById should return audit system when found`(){
     // Given
     val auditSystem = AuditSystem(
         id = "1",
         auditSystemType = AuditSystemType.TASK,
         entityId = "123",
         changeDescription = "change description",
         dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
         changedBy = "Hamsa"
     )
     every { auditSystemRepositoryImp.getAuditSystemById("1") } returns Result.success(auditSystem)


     // When
     val result = auditSystemRepositoryImp.getAuditSystemById("1")

     // Then
     assertThat(result.isSuccess).isTrue()
     assertThat(result.getOrNull()).isEqualTo(Result.success(auditSystem))
     verify(exactly = 1) { auditSystemRepositoryImp.getAuditSystemById("1") }
 }

@Test
fun `getAllAuditSystems should return a list of auditSystem when found`(){
    // Given
    val auditSystem = listOf( AuditSystem(
        id = "1",
        auditSystemType = AuditSystemType.TASK,
        entityId = "123",
        changeDescription = "change description",
        dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        changedBy = "Hamsa"
    )
    )
    every { auditSystemRepositoryImp.getAllAuditSystems() } returns Result.success(auditSystem)

    //When
    val result = auditSystemRepositoryImp.getAllAuditSystems()

    //Then
    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(Result.success(auditSystem))
    verify(exactly = 1) { auditSystemRepositoryImp.getAllAuditSystems() }


}

@Test
fun `getAllAuditSystemsByType should return a list of auditSystem by type when found`(){
    // Given
    val auditSystem = listOf(AuditSystem(
        id = "1",
        auditSystemType = AuditSystemType.TASK,
        entityId = "123",
        changeDescription = "change description",
        dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        changedBy = "Hamsa"
    ))
    val auditSystemType = AuditSystemType.TASK
    every { auditSystemRepositoryImp.getAllAuditSystemsByType(auditSystemType) } returns Result.success(auditSystem)

    // When
    val result = auditSystemRepositoryImp.getAllAuditSystemsByType(auditSystemType)

    // Then
    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(Result.success(auditSystem))
    verify(exactly = 1) { auditSystemRepositoryImp.getAllAuditSystemsByType(auditSystemType) }
}

@Test
fun `getAllAuditSystemsEntityId should return a list of auditSystem by entityId when found`(){

    // Given
    val auditSystem = listOf(AuditSystem(
        id = "1",
        auditSystemType = AuditSystemType.TASK,
        entityId = "123",
        changeDescription = "change description",
        dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        changedBy = "Hamsa"
    ))
    every { auditSystemRepositoryImp.getAllAuditSystemsEntityId("1") } returns Result.success(auditSystem)

    // When
    val result = auditSystemRepositoryImp.getAllAuditSystemsEntityId("1")

    // Then
    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(Result.success(auditSystem))
    verify(exactly = 1) { auditSystemRepositoryImp.getAllAuditSystemsEntityId("1") }
}


}