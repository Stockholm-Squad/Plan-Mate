package data.network.provider

import data.dto.UserAssignedToProjectDto
import org.example.data.utils.USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserAssignedToProjectMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideUserAssignedToProjectCollection(): CoroutineCollection<UserAssignedToProjectDto> =
        mongoDataBase.getCollection(USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME)
}