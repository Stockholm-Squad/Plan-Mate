package org.example.data.source.remote.provider

import com.mongodb.kotlin.client.coroutine.MongoCollection

interface MongoProvider {
    fun <T : Any> provideCollection(
        collectionName: String,
        collectionType: Class<T>,
    ): MongoCollection<T>
}