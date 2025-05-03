package org.example.ui.features.audit

import logic.model.entities.User

interface AuditSystemManagerUi {
    fun invoke(user: User?)
}
