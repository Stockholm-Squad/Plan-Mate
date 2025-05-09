package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel
import org.example.data.database.MATE_TASK_ASSIGNMENT_COLLECTION_NAME
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


class MateTaskAssignmentMongoDataSource(mongoDatabase: CoroutineDatabase) : MateTaskAssignmentDataSource {

    private val collection =
        mongoDatabase.getCollection<MateTaskAssignmentModel>(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

    override suspend fun addUserToTask(mateName: String, taskId: String): Boolean {
        val document = MateTaskAssignmentModel(
            userName = mateName,
            taskId = taskId
        )

        val result = collection.insertOne(document)
        return result.wasAcknowledged()
    }

    override suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean {
        val filter = and(
            MateTaskAssignmentModel::userName eq mateName,
            MateTaskAssignmentModel::taskId eq taskId
        )

        val result = collection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentModel> {
        val filter = MateTaskAssignmentModel::taskId eq taskId
        return collection.find(filter).toList()
    }

    override suspend fun getUsersMateTaskByUserNameId(userName: String): List<MateTaskAssignmentModel> {
        val filter = MateTaskAssignmentModel::userName eq userName
        return collection.find(filter).toList()
    }
}