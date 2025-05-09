package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel

interface MateTaskAssignmentDataSource {
    suspend fun addUserToTask(mateName: String, taskId: String): Boolean
    suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean
    suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentModel>
    suspend fun getUsersMateTaskByUserNameId(userName: String): List<MateTaskAssignmentModel>

    suspend fun getMateTaskAssignmentByUserName(userName: String): List<MateTaskAssignmentModel>?
    suspend fun getMateTaskAssignmentByTaskId(taskId: String): List<MateTaskAssignmentModel>?
    suspend fun getMateTaskAssignment(mateTaskAssignmentModel: MateTaskAssignmentModel): MateTaskAssignmentModel?
    suspend fun addMateTaskAssignment(mateTaskAssignmentModel: MateTaskAssignmentModel): Boolean
    suspend fun deleteMateTaskAssignmentByUserName(userName: String): Boolean
    suspend fun deleteMateTaskAssignmentByTaskId(taskId: String): Boolean
    suspend fun deleteMateTaskAssignment(mateTaskAssignmentModel: MateTaskAssignmentModel): Boolean
}