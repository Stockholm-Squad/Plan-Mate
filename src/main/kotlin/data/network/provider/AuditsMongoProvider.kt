package data.network.provider

import data.dto.AuditSystemDto
import org.example.data.utils.AUDITS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class AuditsMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideAuditsCollection(): CoroutineCollection<AuditSystemDto> =
        mongoDataBase.getCollection(AUDITS_COLLECTION_NAME)
}