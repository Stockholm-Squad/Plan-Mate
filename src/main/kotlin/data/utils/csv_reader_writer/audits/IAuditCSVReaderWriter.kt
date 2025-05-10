package org.example.data.utils.csv_reader_writer.audits

import data.dto.AuditDto

interface IAuditCSVReaderWriter {
    suspend fun read(): List<AuditDto>
    suspend fun overWrite(audits: List<AuditDto>): Boolean
    suspend fun append(audits: List<AuditDto>): Boolean
}