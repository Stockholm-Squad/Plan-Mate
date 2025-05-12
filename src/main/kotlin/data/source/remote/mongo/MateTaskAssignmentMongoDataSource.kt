package data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.MateTaskAssignmentDto

import kotlinx.coroutines.flow.toList
import org.example.data.source.MateTaskAssignmentDataSource
import org.litote.kmongo.and
import org.litote.kmongo.eq

class MateTaskAssignmentMongoDataSource(
    private val mateTaskAssignmentCollection: MongoCollection<MateTaskAssignmentDto>,
) : MateTaskAssignmentDataSource {

    override suspend fun addUserToTask(mateName: String, taskId: String): Boolean =
        mateTaskAssignmentCollection.insertOne(
            MateTaskAssignmentDto(
                userName = mateName,
                taskId = taskId
            )
        ).insertedId != null

    override suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean =
        mateTaskAssignmentCollection.deleteOne(
            and(
                MateTaskAssignmentDto::userName eq mateName,
                MateTaskAssignmentDto::taskId eq taskId
            )
        ).deletedCount > 0

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentCollection.find(MateTaskAssignmentDto::taskId eq taskId).toList()

    override suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentCollection.find(MateTaskAssignmentDto::userName eq userName).toList()
}