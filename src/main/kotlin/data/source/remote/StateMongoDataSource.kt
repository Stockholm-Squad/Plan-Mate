package org.example.data.source.remote

import org.example.data.utils.STATES_COLLECTION_NAME
import data.dto.ProjectStateDto
import org.example.data.source.StateDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue


class StateMongoDataSource(mongoDatabase: CoroutineDatabase) : StateDataSource {

    private val collection = mongoDatabase.getCollection<ProjectStateDto>(STATES_COLLECTION_NAME)

    override suspend fun addProjectState(projectState: ProjectStateDto): Boolean {
        val result = collection.insertOne(projectState)
        return result.wasAcknowledged()
    }

    override suspend fun editProjectState(projectState: ProjectStateDto): Boolean {
        val result = collection.updateOne(
            filter = ProjectStateDto::id eq projectState.id,
            update = setValue(ProjectStateDto::name, projectState.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteProjectState(projectState: ProjectStateDto): Boolean {
        val result = collection.deleteOne(ProjectStateDto::id eq projectState.id)
        return result.deletedCount > 0
    }

    override suspend fun isProjectStateExist(stateName: String): Boolean {
        return collection.findOne(ProjectStateDto::name eq stateName) != null
    }

    override suspend fun getAllProjectStates(): List<ProjectStateDto> {
        return collection.find().toList()
    }

    override suspend fun getProjectStateByName(stateName: String): ProjectStateDto? {
        return collection.findOne(ProjectStateDto::name eq stateName)
    }

    override suspend fun getProjectStateById(stateId: String): ProjectStateDto? {
        return collection.findOne(ProjectStateDto::id eq stateId)
    }
}
