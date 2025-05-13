package org.example.data.source.remote

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.github.cdimascio.dotenv.dotenv
import org.example.data.utils.DATABASE_NAME

object MongoSetup {
    val database: MongoDatabase
        get() = MongoClient.create(
            getClientMongoSettings()
        ).getDatabase(DATABASE_NAME)

    private fun getClientMongoSettings(): MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(dotenv()["MONGO_URI"]))
        .serverApi(
            getServiceApi()
        )
        .build()

    private fun getServiceApi(): ServerApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
}
