package org.example.di

import org.example.data.database.MongoSetup
import org.example.data.datasources.audit_system_data_source.AuditSystemMongoDataSource
import org.example.data.datasources.audit_system_data_source.IAuditSystemDataSource
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.mate_task_assignment_data_source.MateTaskAssignmentMongoDataSource
import org.example.data.datasources.project_data_source.IProjectDataSource
import org.example.data.datasources.project_data_source.ProjectMongoDataSource
import org.example.data.datasources.state_data_source.IStateDataSource
import org.example.data.datasources.state_data_source.StateMongoDataSource
import org.example.data.datasources.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.datasources.task_In_project_data_source.TaskInProjectMongoDataSource
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.datasources.task_data_source.TaskMongoDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.UserAssignedToProjectMongoDataSource
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.datasources.user_data_source.UserMongoDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val dataBaseModule = module {
    single<CoroutineDatabase> { MongoSetup.database }
    single<IAuditSystemDataSource> { AuditSystemMongoDataSource(get()) }
    single<IProjectDataSource> { ProjectMongoDataSource(get()) }
    single<ITaskDataSource> { TaskMongoDataSource(get()) }
    single<IStateDataSource> { StateMongoDataSource(get()) }
    single<IUserDataSource> { UserMongoDataSource(get()) }
    single<ITaskInProjectDataSource> { TaskInProjectMongoDataSource(get()) }
    single<IMateTaskAssignmentDataSource> { MateTaskAssignmentMongoDataSource(get()) }
    single<IUserAssignedToProjectDataSource> { UserAssignedToProjectMongoDataSource(get()) }
}