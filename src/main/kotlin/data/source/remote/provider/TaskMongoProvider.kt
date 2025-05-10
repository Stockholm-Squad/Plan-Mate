package org.example.data.source.remote.provider

import data.dto.TaskDto
import org.example.data.utils.TASKS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class TaskMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideTaskCollection(): CoroutineCollection<TaskDto> =
        mongoDataBase.getCollection(TASKS_COLLECTION_NAME)
}