package org.example.di

import org.example.data.source.UserDataSource
import org.example.data.MongoSetup
import org.example.data.source.remote.AuditSystemMongoDataSource
import org.example.data.datasources.IAuditSystemDataSource
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.remote.MateTaskAssignmentMongoDataSource
import org.example.data.source.ProjectDataSource
import org.example.data.source.remote.ProjectMongoDataSource
import org.example.data.source.StateDataSource
import org.example.data.source.remote.StateMongoDataSource
import org.example.data.source.TaskInProjectDataSource
import org.example.data.source.remote.TaskInProjectMongoDataSource
import org.example.data.source.TaskDataSource
import org.example.data.source.remote.TaskMongoDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.remote.UserAssignedToProjectMongoDataSource
import org.example.data.source.remote.UserMongoDataSource
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val dataBaseModule = module {
    single<CoroutineDatabase> { MongoSetup.createDataBase() }
    single<IAuditSystemDataSource> { AuditSystemMongoDataSource(get()) }
    single<ProjectDataSource> { ProjectMongoDataSource(get(),get()) }
    single<TaskDataSource> { TaskMongoDataSource(get()) }
    single<StateDataSource> { StateMongoDataSource(get()) }
    single<UserDataSource> { UserMongoDataSource(get(),get(),) }
    single<TaskInProjectDataSource> { TaskInProjectMongoDataSource(get()) }
    single<MateTaskAssignmentDataSource> { MateTaskAssignmentMongoDataSource(get()) }
    single<UserAssignedToProjectDataSource> { UserAssignedToProjectMongoDataSource(get()) }
}