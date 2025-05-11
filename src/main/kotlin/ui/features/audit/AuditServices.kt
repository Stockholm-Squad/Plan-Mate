package org.example.ui.features.audit

import kotlinx.coroutines.runBlocking
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.EntityType
import org.example.logic.usecase.audit.AddAuditUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.output.OutputPrinter
import java.util.*

class AuditServices(
    private val addAuditUseCase: AddAuditUseCase,
    private val loginUseCase: LoginUseCase,
    private val printer: OutputPrinter
) {
    fun addAuditForAddEntity(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String? = null
    ) = runBlocking {
        getCurrentUserId()?.let { userId ->
            val auditDescription = printer.printAddEntityDescription(
                entityType = entityType,
                entityName = entityName,
                entityId = entityId,
                additionalInfo = additionalInfo ?: ""
            )
            addAuditUseCase.addAudit(userId, entityType, entityId, auditDescription)
        } ?: run {
            printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)
        }
    }

    fun addAuditForUpdateEntity(
        entityType: EntityType,
        existEntityName: String,
        newEntityName: String,
        entityId: UUID,
        newDescription: String? = null,
        newStateName: String? = null
    ) = runBlocking {
        getCurrentUserId()?.let { userId ->
            val auditDescription = printer.printUpdateEntityDescription(
                entityType = entityType,
                existEntityName = existEntityName,
                newEntityName = newEntityName,
                entityId = entityId,
                newDescription = newDescription ?: "",
                newStateName = newStateName ?: ""
            )
            addAuditUseCase.addAudit(userId, entityType, entityId, auditDescription)
        } ?: run {
            printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)
        }
    }

    fun addAuditForDeleteEntity(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String? = null
    ) = runBlocking {
        getCurrentUserId()?.let { userId ->
            val auditDescription = printer.printDeleteEntityDescription(
                entityType = entityType,
                entityName = entityName,
                entityId = entityId,
                additionalInfo = additionalInfo ?: ""
            )
            addAuditUseCase.addAudit(userId, entityType, entityId, auditDescription)
        } ?: printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)
    }

    private fun getCurrentUserId(): UUID? =
        loginUseCase.getCurrentUser()?.id ?: run {
            printer.showMessage(UiMessages.USER_NOT_LOGGED_IN)
            null
        }
}
