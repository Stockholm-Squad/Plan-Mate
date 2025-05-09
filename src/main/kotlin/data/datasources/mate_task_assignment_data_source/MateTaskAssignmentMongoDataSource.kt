package org.example.data.datasources.mate_task_assignment_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import data.models.MateTaskAssignmentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
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

    override suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentModel> {
        val filter = MateTaskAssignmentModel::userName eq userName
        return collection.find(filter).toList()
    }

    override suspend fun getMateTaskAssignmentByUserName(userName: String): List<MateTaskAssignmentModel>? {
        return collection.find(MateTaskAssignmentModel::userName eq userName).toList()
    }

    override suspend fun getMateTaskAssignmentByTaskId(taskId: String): List<MateTaskAssignmentModel>? {
        return collection.find(MateTaskAssignmentModel::taskId eq taskId).toList()
    }

    override suspend fun getMateTaskAssignment(mateTaskAssignmentModel: MateTaskAssignmentModel): MateTaskAssignmentModel? {
        val filter = Document(MateTaskAssignmentModel::userName.toString(), mateTaskAssignmentModel.userName)
            .append(MateTaskAssignmentModel::taskId.toString(), mateTaskAssignmentModel.taskId)
        val result = collection.find(filter).first()

        return result?.let {
            MateTaskAssignmentModel(
                userName = it.userName,
                taskId = it.taskId
            )
        }
    }

    override suspend fun addMateTaskAssignment(mateTaskAssignmentModel: MateTaskAssignmentModel): Boolean {
        val result = collection.insertOne(mateTaskAssignmentModel)
        return result.wasAcknowledged()
    }

    override suspend fun deleteMateTaskAssignmentByUserName(userName: String): Boolean {
        val result = collection.deleteMany(MateTaskAssignmentModel::userName eq userName)
        return result.deletedCount > 0
    }

    override suspend fun deleteMateTaskAssignmentByTaskId(taskId: String): Boolean {
        val result = collection.deleteMany(MateTaskAssignmentModel::taskId eq taskId)
        return result.deletedCount > 0
    }

    override suspend fun deleteMateTaskAssignment(mateTaskAssignmentModel: MateTaskAssignmentModel): Boolean {
        val filter = Document(MateTaskAssignmentModel::userName.toString(), mateTaskAssignmentModel.userName)
            .append(MateTaskAssignmentModel::taskId.toString(), mateTaskAssignmentModel.taskId)

        return collection.deleteOne(filter).deletedCount > 0
    }

}