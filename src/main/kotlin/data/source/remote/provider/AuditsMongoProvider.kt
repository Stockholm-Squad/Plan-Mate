package org.example.data.source.remote.provider

import data.dto.AuditDto
import org.example.data.utils.AUDITS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class AuditsMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideAuditsCollection(): CoroutineCollection<AuditDto> =
        mongoDataBase.getCollection(AUDITS_COLLECTION_NAME)
}