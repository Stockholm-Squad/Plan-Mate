package org.example.data.source.local

import data.dto.AuditDto
import org.example.data.source.AuditDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class AuditCSVDataSource(
    private val auditReaderWriter: IReaderWriter<AuditDto>,
) : AuditDataSource {
    override suspend fun addAudit(audit: AuditDto): Boolean =
        auditReaderWriter.append(listOf(audit))

    override suspend fun getAllAudits(): List<AuditDto> =
        auditReaderWriter.read()
}