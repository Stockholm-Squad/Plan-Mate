package org.example.data.source.local

import data.dto.MateTaskAssignmentDto
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class MateTaskAssignmentCSVDataSource(
    private val mateTaskAssignmentReaderWriter: IReaderWriter<MateTaskAssignmentDto>,
) : MateTaskAssignmentDataSource {
    override suspend fun addUserToTask(username: String, taskId: String): Boolean =
        mateTaskAssignmentReaderWriter.append(listOf(MateTaskAssignmentDto(username, taskId)))

    override suspend fun deleteUserFromTask(username: String, taskId: String): Boolean =
        mateTaskAssignmentReaderWriter.read().filterNot { mateTaskAssignment -> mateTaskAssignment.taskId == taskId }
            .let { filteredMateTaskAssignment -> mateTaskAssignmentReaderWriter.overWrite(filteredMateTaskAssignment) }

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentReaderWriter.read().filter { mateTaskAssignment -> mateTaskAssignment.taskId == taskId }

    override suspend fun getUsersMateTaskByUserName(username: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentReaderWriter.read().filter { mateTaskAssignment -> mateTaskAssignment.username == username }
}