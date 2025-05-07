package org.example.data.datasources

import org.example.data.utils.DATABASE_NAME
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoSetup {

    private const val CONNECTION_STRING =
        "mongodb+srv://PlanMate:planmate@planmate.06s0n1e.mongodb.net/?retryWrites=true&w=majority&appName=PlanMate"

    val client: CoroutineClient = KMongo.createClient(CONNECTION_STRING).coroutine
    val database: CoroutineDatabase = client.getDatabase(DATABASE_NAME)
}
