package org.example.data.source.remote

import org.example.data.utils.STATES_COLLECTION_NAME
import data.dto.ProjectStateModel
import org.example.data.source.StateDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue


class StateMongoDataSource(mongoDatabase: CoroutineDatabase) : StateDataSource {

    private val collection = mongoDatabase.getCollection<ProjectStateModel>(STATES_COLLECTION_NAME)

    override suspend fun addProjectState(projectState: ProjectStateModel): Boolean {
        val result = collection.insertOne(projectState)
        return result.wasAcknowledged()
    }

    override suspend fun editProjectState(projectState: ProjectStateModel): Boolean {
        val result = collection.updateOne(
            filter = ProjectStateModel::id eq projectState.id,
            update = setValue(ProjectStateModel::name, projectState.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteProjectState(projectState: ProjectStateModel): Boolean {
        val result = collection.deleteOne(ProjectStateModel::id eq projectState.id)
        return result.deletedCount > 0
    }

    override suspend fun isProjectStateExist(stateName: String): Boolean {
        return collection.findOne(ProjectStateModel::name eq stateName) != null
    }

    override suspend fun getAllProjectStates(): List<ProjectStateModel> {
        return collection.find().toList()
    }

    override suspend fun getProjectStateByName(stateName: String): ProjectStateModel? {
        return collection.findOne(ProjectStateModel::name eq stateName)
    }

    override suspend fun getProjectStateById(stateId: String): ProjectStateModel? {
        return collection.findOne(ProjectStateModel::id eq stateId)
    }
}
