package org.example.data.datasources

import data.dto.MateTaskAssignmentDto

interface IMateTaskAssignmentCSVReaderWriter {
    suspend fun read(): List<MateTaskAssignmentDto>
    suspend fun overWrite(mateTasks: List<MateTaskAssignmentDto>): Boolean
    suspend fun append(mateTasks: List<MateTaskAssignmentDto>): Boolean
}