package data.source.remote.mongo

import data.dto.ProjectStateDto
import org.example.data.source.StateDataSource
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue


class StateMongoDataSource(
    private val stateCollection: CoroutineCollection<ProjectStateDto>
) : StateDataSource {

    override suspend fun addProjectState(projectState: ProjectStateDto): Boolean {
        val result = stateCollection.insertOne(projectState)
        return result.wasAcknowledged()
    }

    override suspend fun editProjectState(projectState: ProjectStateDto): Boolean {
        val result = stateCollection.updateOne(
            filter = ProjectStateDto::id eq projectState.id,
            update = setValue(ProjectStateDto::name, projectState.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteProjectState(projectState: ProjectStateDto): Boolean {
        val result = stateCollection.deleteOne(ProjectStateDto::id eq projectState.id)
        return result.deletedCount > 0
    }

    override suspend fun isProjectStateExist(stateName: String): Boolean {
        return stateCollection.findOne(ProjectStateDto::name eq stateName) != null
    }

    override suspend fun getAllProjectStates(): List<ProjectStateDto> {
        return stateCollection.find().toList()
    }

    override suspend fun getProjectStateByName(stateName: String): ProjectStateDto? {
        return stateCollection.findOne(ProjectStateDto::name eq stateName)
    }

    override suspend fun getProjectStateById(stateId: String): ProjectStateDto? {
        return stateCollection.findOne(ProjectStateDto::id eq stateId)
    }
}
