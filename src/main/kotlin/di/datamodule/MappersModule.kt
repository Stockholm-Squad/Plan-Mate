package org.example.di.datamodule

import data.mapper.AuditSystemMapper
import org.example.data.mapper.ProjectMapper
import org.example.data.mapper.StateMapper
import org.example.data.mapper.TaskMapper
import org.example.data.mapper.UserMapper
import org.koin.dsl.module

val mappersModule = module {
    factory<AuditSystemMapper> { AuditSystemMapper() }
    factory<ProjectMapper> { ProjectMapper() }
    factory<StateMapper> { StateMapper() }
    factory<TaskMapper> { TaskMapper() }
    factory<UserMapper> { UserMapper() }
}