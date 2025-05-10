package org.example.data.source.local

import data.dto.MateTaskAssignmentDto
import org.example.data.source.MateTaskAssignmentDataSource

class MateTaskAssignmentCSVDataSource : MateTaskAssignmentDataSource {
    override suspend fun addUserToTask(mateName: String, taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getMateTaskAssignmentByUserName(userName: String): List<MateTaskAssignmentDto>? {
        TODO("Not yet implemented")
    }

    override suspend fun getMateTaskAssignmentByTaskId(taskId: String): List<MateTaskAssignmentDto>? {
        TODO("Not yet implemented")
    }

    override suspend fun getMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): MateTaskAssignmentDto? {
        TODO("Not yet implemented")
    }

    override suspend fun addMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMateTaskAssignmentByUserName(userName: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMateTaskAssignmentByTaskId(taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean {
        TODO("Not yet implemented")
    }
}