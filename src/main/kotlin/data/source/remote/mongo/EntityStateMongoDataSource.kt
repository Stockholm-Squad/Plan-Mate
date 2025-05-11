package data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.EntityStateDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.EntityStateDataSource
import org.litote.kmongo.eq
import org.litote.kmongo.setValue


class EntityStateMongoDataSource(
    private val stateCollection: MongoCollection<EntityStateDto>,
) : EntityStateDataSource {

    override suspend fun addEntityState(entityState: EntityStateDto): Boolean {
        val result = stateCollection.insertOne(entityState)
        return result.wasAcknowledged()
    }

    override suspend fun editEntityState(entityState: EntityStateDto): Boolean {
        val result = stateCollection.updateOne(
            filter = EntityStateDto::id eq entityState.id,
            update = setValue(EntityStateDto::name, entityState.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteEntityState(entityState: EntityStateDto): Boolean {
        val result = stateCollection.deleteOne(EntityStateDto::id eq entityState.id)
        return result.deletedCount > 0
    }

    override suspend fun isEntityStateExist(stateName: String): Boolean {
        val filteredQuery = Filters.eq(EntityStateDto::name.name, stateName)
        return stateCollection.find(filteredQuery).firstOrNull() != null
    }

    override suspend fun getAllEntityStates(): List<EntityStateDto> {
        return stateCollection.find().toList()
    }

    override suspend fun getEntityStateByName(stateName: String): EntityStateDto? {
        val filteredQuery = Filters.eq(EntityStateDto::name.name, stateName)
        return stateCollection.find(filteredQuery).firstOrNull()
    }

    override suspend fun getEntityStateById(stateId: String): EntityStateDto? {
        val filteredQuery = Filters.eq(EntityStateDto::id.name, stateId)
        return stateCollection.find(filteredQuery).firstOrNull()
    }
}
