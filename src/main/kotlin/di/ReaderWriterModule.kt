package org.example.di

import data.dto.*
import org.example.data.source.local.csv_reader_writer.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val readerWriterModule = module {
    factory<IReaderWriter<AuditDto>>(named("auditReaderWriter")) { AuditCSVReaderWriter(filePath = "audits.csv") }
    factory<IReaderWriter<ProjectDto>>(named("projectReaderWriter")) { ProjectCSVReaderWriter(filePath = "projects.csv") }
    factory<IReaderWriter<EntityStateDto>>(named("entityStateReaderWriter")) { EntityStateCSVReaderWriter(filePath = "state.csv") }
    factory<IReaderWriter<TaskDto>>(named("taskReaderWriter")) { TaskCSVReaderWriter(filePath = "task.csv") }
    factory<IReaderWriter<UserDto>>(named("userReaderWriter")) { UserCSVReaderWriter(filePath = "users.csv", get()) }
    factory<IReaderWriter<TaskInProjectDto>>(named("taskInProjectReaderWriter")) { TaskInProjectCSVReaderWriter(filePath = "task_in_project.csv") }
    factory<IReaderWriter<UserAssignedToProjectDto>>(named("userAssignedToProjectReaderWriter")) {
        UserAssignedToProjectCSVReaderWriter(
            filePath = "user_assigned_to_project.csv"
        )
    }
    factory<IReaderWriter<MateTaskAssignmentDto>>(named("mateTaskAssignmentReaderWriter")) {
        MateTaskAssignmentCSVReaderWriter(
            filePath = "mate_task_assignment.csv"
        )
    }
}
