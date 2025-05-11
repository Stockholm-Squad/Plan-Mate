package org.example.data.source.remote.provider

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase

class MongoProviderImpl(
    private val mongoDataBase: MongoDatabase,
) : MongoProvider {

    override fun <T : Any> provideCollection(
        collectionName: String,
        collectionType: Class<T>,
    ): MongoCollection<T> =
        mongoDataBase.getCollection(collectionName, collectionType)

}
