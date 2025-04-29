package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class ManageAuditSystemUseCase(private val auditSystemRepository: AuditSystemRepository) {
    fun addChangeInTask() : Boolean {
        return false
    }

    fun addChangeInProject() : Boolean {
        return false
    }

    fun getTaskChanges() : List<AuditSystem> {
        return emptyList()
    }

    fun getProjectChanges() : List<AuditSystem> {
        return emptyList()
    }

    fun getChangesByUser() : List<AuditSystem> {
        return emptyList()
    }

    fun clearChanges() : Boolean {
        return false
    }

}