package org.example.data.source.remote

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.utils.CONNECTION_STRING
import org.example.data.utils.DATABASE_NAME

object MongoSetup {

    fun createDataBase(): MongoDatabase {

        val serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build()

        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(CONNECTION_STRING))
            .serverApi(serverApi)
            .build()

        val client: MongoClient = MongoClient.create(settings)

        return client.getDatabase(DATABASE_NAME)
    }
}
