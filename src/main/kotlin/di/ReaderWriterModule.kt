package org.example.di

import org.example.data.source.local.csv_reader_writer.audits.AuditCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.audits.IAuditCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.mate_task_assignment.IMateTaskAssignmentCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.mate_task_assignment.MateTaskAssignmentCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.project.IProjectCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.project.ProjectCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.state.EntityStateCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.state.IEntityStateCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.task.ITaskCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.task.TaskCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.task_in_project.ITaskInProjectCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.task_in_project.TaskInProjectCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.user.IUserCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.user.UserCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.user_assigned_to_project.IUserAssignedToProjectCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.user_assigned_to_project.UserAssignedToProjectCSVReaderWriter
import org.koin.dsl.module

val localDatasourceModule = module {
    factory<IAuditCSVReaderWriter> { AuditCSVReaderWriter(filePath = "audits.csv") }
    factory<IProjectCSVReaderWriter> { ProjectCSVReaderWriter(filePath = "projects.csv") }
    factory<IEntityStateCSVReaderWriter> { EntityStateCSVReaderWriter(filePath = "state.csv") }
    factory<ITaskCSVReaderWriter> { TaskCSVReaderWriter(filePath = "task.csv") }
    factory<IUserCSVReaderWriter> { UserCSVReaderWriter(filePath = "users.csv", get()) }
    factory<ITaskInProjectCSVReaderWriter> { TaskInProjectCSVReaderWriter(filePath = "task_in_project.csv") }
    factory<IUserAssignedToProjectCSVReaderWriter> { UserAssignedToProjectCSVReaderWriter(filePath = "user_assigned_to_project.csv") }
    factory<IMateTaskAssignmentCSVReaderWriter> { MateTaskAssignmentCSVReaderWriter(filePath = "mate_task_assignment.csv") }
}
