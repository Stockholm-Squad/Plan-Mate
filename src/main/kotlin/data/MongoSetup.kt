package org.example.data

import org.example.data.utils.CONNECTION_STRING
import org.example.data.utils.DATABASE_NAME
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoSetup {

    val client: CoroutineClient = KMongo.createClient(CONNECTION_STRING).coroutine
    val database: CoroutineDatabase = client.getDatabase(DATABASE_NAME)
}
