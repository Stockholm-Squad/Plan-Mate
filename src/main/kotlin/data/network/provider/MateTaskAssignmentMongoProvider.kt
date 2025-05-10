package data.network.provider

import data.dto.MateTaskAssignmentDto
import org.example.data.utils.MATE_TASK_ASSIGNMENT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MateTaskAssignmentMongoProvider(
    private val mongoDataBase: CoroutineDatabase
) {
    fun provideMateTaskAssignmentCollection(): CoroutineCollection<MateTaskAssignmentDto> =
        mongoDataBase.getCollection(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

}