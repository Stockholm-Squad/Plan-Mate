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

    override suspend fun addEntityState(entityState: EntityStateDto): Boolean =
        stateCollection.insertOne(entityState).insertedId != null

    override suspend fun updateEntityState(entityState: EntityStateDto): Boolean =
        stateCollection.updateOne(
            EntityStateDto::id eq entityState.id,
            setValue(EntityStateDto::name, entityState.name)
        ).matchedCount > 0

    override suspend fun deleteEntityState(entityState: EntityStateDto): Boolean =
        stateCollection.deleteOne(EntityStateDto::id eq entityState.id).deletedCount > 0

    override suspend fun isEntityStateExist(stateName: String): Boolean =
        stateCollection.find(Filters.eq(EntityStateDto::name.name, stateName)).firstOrNull() != null

    override suspend fun getAllEntityStates(): List<EntityStateDto> =
        stateCollection.find().toList()

    override suspend fun getEntityStateByName(stateName: String): EntityStateDto? =
        stateCollection.find(Filters.eq(EntityStateDto::name.name, stateName)).firstOrNull()

    override suspend fun getEntityStateById(stateId: String): EntityStateDto? =
        stateCollection.find(Filters.eq(EntityStateDto::id.name, stateId)).firstOrNull()
}