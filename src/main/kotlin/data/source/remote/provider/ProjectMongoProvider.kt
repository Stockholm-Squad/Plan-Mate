package org.example.data.source.remote.provider

import data.dto.ProjectDto
import org.example.data.utils.PROJECTS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class ProjectMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideProjectCollection(): CoroutineCollection<ProjectDto> =
        mongoDataBase.getCollection(PROJECTS_COLLECTION_NAME)
}