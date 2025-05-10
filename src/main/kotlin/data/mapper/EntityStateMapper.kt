package org.example.data.mapper

import data.dto.EntityStateDto
import org.example.logic.entities.EntityState
import org.example.logic.utils.toSafeUUID

fun EntityStateDto.mapToStateEntity(): EntityState? {
    return EntityState(id.toSafeUUID() ?: return null, name)
}


fun EntityState.mapToStateModel(): EntityStateDto {
    return EntityStateDto(id.toString(), name)
}


