package org.example.data.source.remote.provider

import data.dto.ProjectStateDto
import org.example.data.utils.STATES_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class StateMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideStatesCollection(): CoroutineCollection<ProjectStateDto> =
        mongoDataBase.getCollection(STATES_COLLECTION_NAME)
}