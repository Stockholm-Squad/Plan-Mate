package data.source.remote.mongo

import data.dto.EntityStateDto
import org.example.data.source.EntityStateDataSource
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue


class EntityStateMongoDataSource(
    private val stateCollection: CoroutineCollection<EntityStateDto>
) : EntityStateDataSource {

    override suspend fun addEntityState(projectState: EntityStateDto): Boolean {
        val result = stateCollection.insertOne(projectState)
        return result.wasAcknowledged()
    }

    override suspend fun editEntityState(projectState: EntityStateDto): Boolean {
        val result = stateCollection.updateOne(
            filter = EntityStateDto::id eq projectState.id,
            update = setValue(EntityStateDto::name, projectState.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteEntityState(projectState: EntityStateDto): Boolean {
        val result = stateCollection.deleteOne(EntityStateDto::id eq projectState.id)
        return result.deletedCount > 0
    }

    override suspend fun isEntityStateExist(stateName: String): Boolean {
        return stateCollection.findOne(EntityStateDto::name eq stateName) != null
    }

    override suspend fun getAllEntityStates(): List<EntityStateDto> {
        return stateCollection.find().toList()
    }

    override suspend fun getEntityStateByName(stateName: String): EntityStateDto? {
        return stateCollection.findOne(EntityStateDto::name eq stateName)
    }

    override suspend fun getEntityStateById(stateId: String): EntityStateDto? {
        return stateCollection.findOne(EntityStateDto::id eq stateId)
    }
}
