package org.example.ui.features.audit

import logic.models.entities.User

interface AuditSystemManagerUi {
    fun invoke(user: User?)
}
