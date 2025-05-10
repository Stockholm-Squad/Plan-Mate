package org.example.data.source.remote.impl

import data.dto.MateTaskAssignmentDto
import org.bson.Document
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

    override suspend fun getMateTaskAssignmentByUserName(userName: String): List<MateTaskAssignmentDto>? {
        return mateTaskAssignmentCollection.find(MateTaskAssignmentDto::userName eq userName).toList()
    }

    override suspend fun getMateTaskAssignmentByTaskId(taskId: String): List<MateTaskAssignmentDto>? {
        return mateTaskAssignmentCollection.find(MateTaskAssignmentDto::taskId eq taskId).toList()
    }

    override suspend fun getMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): MateTaskAssignmentDto? {
        val filter = Document(MateTaskAssignmentDto::userName.toString(), mateTaskAssignmentDto.userName)
            .append(MateTaskAssignmentDto::taskId.toString(), mateTaskAssignmentDto.taskId)
        val result = mateTaskAssignmentCollection.find(filter).first()

        return result?.let {
            MateTaskAssignmentDto(
                userName = it.userName,
                taskId = it.taskId
            )
        }
    }

    override suspend fun addMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean {
        val result = mateTaskAssignmentCollection.insertOne(mateTaskAssignmentDto)
        return result.wasAcknowledged()
    }

    override suspend fun deleteMateTaskAssignmentByUserName(userName: String): Boolean {
        val result = mateTaskAssignmentCollection.deleteMany(MateTaskAssignmentDto::userName eq userName)
        return result.deletedCount > 0
    }

    override suspend fun deleteMateTaskAssignmentByTaskId(taskId: String): Boolean {
        val result = mateTaskAssignmentCollection.deleteMany(MateTaskAssignmentDto::taskId eq taskId)
        return result.deletedCount > 0
    }

    override suspend fun deleteMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean {
        val filter = Document(MateTaskAssignmentDto::userName.toString(), mateTaskAssignmentDto.userName)
            .append(MateTaskAssignmentDto::taskId.toString(), mateTaskAssignmentDto.taskId)

        return mateTaskAssignmentCollection.deleteOne(filter).deletedCount > 0
    }

}