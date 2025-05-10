package org.example.data.source.local.csv_reader_writer.mate_task_assignment

import data.dto.MateTaskAssignmentDto

interface IMateTaskAssignmentCSVReaderWriter {
    suspend fun read(): List<MateTaskAssignmentDto>
    suspend fun overWrite(mateTasks: List<MateTaskAssignmentDto>): Boolean
    suspend fun append(mateTasks: List<MateTaskAssignmentDto>): Boolean
}