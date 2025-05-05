package org.example.data.datasources

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoSetup {
    private const val CONNECTION_STRING =
        "mongodb+srv://asmaa:S6H6nUZ1JMYyQ1Xq@cluster0.6yjjmpq.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    val client = KMongo.createClient(CONNECTION_STRING).coroutine
    val database = client.getDatabase("PlanMate")
}