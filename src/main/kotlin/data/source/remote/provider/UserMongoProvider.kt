package org.example.data.source.remote.provider

import data.dto.UserDto
import org.example.data.utils.USERS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideUserCollection(): CoroutineCollection<UserDto> =
        mongoDataBase.getCollection(USERS_COLLECTION_NAME)
}