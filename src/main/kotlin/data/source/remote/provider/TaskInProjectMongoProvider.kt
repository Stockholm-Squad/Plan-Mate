package org.example.data.source.remote.provider

import data.dto.TaskInProjectDto
import org.example.data.utils.TASK_IN_PROJECT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class TaskInProjectMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideTaskInProjectCollection(): CoroutineCollection<TaskInProjectDto> =
        mongoDataBase.getCollection(TASK_IN_PROJECT_COLLECTION_NAME)
}