package org.example.data.source

import data.dto.MateTaskAssignmentDto

interface MateTaskAssignmentDataSource {
    suspend fun addUserToTask(mateName: String, taskId: String): Boolean
    suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean
    suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto>
    suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentDto>

    suspend fun getMateTaskAssignmentByUserName(userName: String): List<MateTaskAssignmentDto>?
    suspend fun getMateTaskAssignmentByTaskId(taskId: String): List<MateTaskAssignmentDto>?
    suspend fun getMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): MateTaskAssignmentDto?
    suspend fun addMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean
    suspend fun deleteMateTaskAssignmentByUserName(userName: String): Boolean
    suspend fun deleteMateTaskAssignmentByTaskId(taskId: String): Boolean
    suspend fun deleteMateTaskAssignment(mateTaskAssignmentDto: MateTaskAssignmentDto): Boolean
}