package org.example.data.source.remote

import data.dto.MateTaskAssignmentDto
import org.bson.Document
import org.example.data.utils.MATE_TASK_ASSIGNMENT_COLLECTION_NAME
import org.example.data.source.MateTaskAssignmentDataSource
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


class MateTaskAssignmentMongoDataSource(mongoDatabase: CoroutineDatabase) : MateTaskAssignmentDataSource {

    private val collection =
        mongoDatabase.getCollection<MateTaskAssignmentDto>(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

    override suspend fun addUserToTask(mateName: String, taskId: String): Boolean {
        val document = MateTaskAssignmentDto(
            userName = mateName,
            taskId = taskId
        )

        val result = collection.insertOne(document)
        return result.wasAcknowledged()
    }

    override suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean {
        val filter = and(
            MateTaskAssignmentDto::userName eq mateName,
            MateTaskAssignmentDto::taskId eq taskId
        )

        val result = collection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> {
        val filter = MateTaskAssignmentDto::taskId eq taskId
        return collection.find(filter).toList()
    }

    override suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentDto> {
        val filter = MateTaskAssignmentDto::userName eq userName
        return collection.find(filter).toList()
    }

    override suspend fun getMateTaskAssignmentByUserName(userName: String): List<MateTaskAssignmentDto>? {
        return collection.find(MateTaskAssignmentDto::userName eq userName).toList()
    }

    override suspend fun getMateTaskAssignmentByTaskId(taskId: String): List<MateTaskAssignmentDto>? {
        return collection.find(MateTaskAssignmentDto::taskId eq taskId).toList()
    }

    override suspend fun getMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): MateTaskAssignmentDto? {
        val filter = Document(MateTaskAssignmentDto::userName.toString(), mateTaskAssignmentDto.userName)
            .append(MateTaskAssignmentDto::taskId.toString(), mateTaskAssignmentDto.taskId)
        val result = collection.find(filter).first()

        return result?.let {
            MateTaskAssignmentDto(
                userName = it.userName,
                taskId = it.taskId
            )
        }
    }

    override suspend fun addMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean {
        val result = collection.insertOne(mateTaskAssignmentDto)
        return result.wasAcknowledged()
    }

    override suspend fun deleteMateTaskAssignmentByUserName(userName: String): Boolean {
        val result = collection.deleteMany(MateTaskAssignmentDto::userName eq userName)
        return result.deletedCount > 0
    }

    override suspend fun deleteMateTaskAssignmentByTaskId(taskId: String): Boolean {
        val result = collection.deleteMany(MateTaskAssignmentDto::taskId eq taskId)
        return result.deletedCount > 0
    }

    override suspend fun deleteMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean {
        val filter = Document(MateTaskAssignmentDto::userName.toString(), mateTaskAssignmentDto.userName)
            .append(MateTaskAssignmentDto::taskId.toString(), mateTaskAssignmentDto.taskId)

        return collection.deleteOne(filter).deletedCount > 0
    }

}