package data.source.remote.mongo

import data.dto.MateTaskAssignmentDto

import org.example.data.source.MateTaskAssignmentDataSource
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq


class MateTaskAssignmentMongoDataSource(
    private val mateTaskAssignmentCollection: CoroutineCollection<MateTaskAssignmentDto>
) : MateTaskAssignmentDataSource {

    override suspend fun addUserToTask(mateName: String, taskId: String): Boolean {
        val document = MateTaskAssignmentDto(
            userName = mateName,
            taskId = taskId
        )

        val result = mateTaskAssignmentCollection.insertOne(document)
        return result.wasAcknowledged()
    }

    override suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean {
        val filter = and(
            MateTaskAssignmentDto::userName eq mateName,
            MateTaskAssignmentDto::taskId eq taskId
        )
        val result = mateTaskAssignmentCollection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> {
        val filter = MateTaskAssignmentDto::taskId eq taskId
        return mateTaskAssignmentCollection.find(filter).toList()
    }

    override suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentDto> {
        val filter = MateTaskAssignmentDto::userName eq userName
        return mateTaskAssignmentCollection.find(filter).toList()
    }
}