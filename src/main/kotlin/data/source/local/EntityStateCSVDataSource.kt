package org.example.data.source.local

import data.dto.EntityStateDto
import org.example.data.source.EntityStateDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class EntityStateCSVDataSource(
    private val entityStateReaderWriter: IReaderWriter<EntityStateDto>,
) : EntityStateDataSource {
    override suspend fun addEntityState(entityState: EntityStateDto): Boolean =
        entityStateReaderWriter.append(listOf(entityState))

    override suspend fun editEntityState(entityState: EntityStateDto): Boolean =
        getAllEntityStates().map { state -> if (state.id == entityState.id) entityState else state }
            .let { updatedEntityStates -> entityStateReaderWriter.overWrite(updatedEntityStates) }

    override suspend fun deleteEntityState(entityState: EntityStateDto): Boolean =
        getAllEntityStates().filterNot { state -> state.id == entityState.id }
            .let { filteredEntityStates -> entityStateReaderWriter.overWrite(filteredEntityStates) }

    override suspend fun isEntityStateExist(stateName: String): Boolean =
        getEntityStateById(stateName)?.let { true } ?: false

    override suspend fun getAllEntityStates(): List<EntityStateDto> =
        entityStateReaderWriter.read()

    override suspend fun getEntityStateByName(stateName: String): EntityStateDto? =
        getAllEntityStates().find { state -> state.name == stateName }

    override suspend fun getEntityStateById(stateId: String): EntityStateDto? =
        getAllEntityStates().find { state -> state.id == stateId }
}