package di

import data.source.remote.mongo.*
import org.example.data.source.remote.provider.AuditsMongoProvider
import org.example.data.source.remote.provider.ProjectMongoProvider
import org.example.data.source.remote.provider.StateMongoProvider
import org.example.data.source.remote.provider.TaskMongoProvider
import org.example.data.source.remote.provider.UserMongoProvider
import org.example.data.source.remote.provider.TaskInProjectMongoProvider
import org.example.data.source.remote.provider.MateTaskAssignmentMongoProvider
import org.example.data.source.remote.provider.UserAssignedToProjectMongoProvider

import org.example.data.source.*
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single<AuditDataSource> { AuditMongoDataSource(get<AuditsMongoProvider>().provideAuditsCollection()) }
    single<ProjectDataSource> { ProjectMongoDataSource(get<ProjectMongoProvider>().provideProjectCollection(), get()) }
    single<TaskDataSource> { TaskMongoDataSource(get<TaskMongoProvider>().provideTaskCollection()) }
    single<StateDataSource> { StateMongoDataSource(get<StateMongoProvider>().provideStatesCollection()) }
    single<UserDataSource> { UserMongoDataSource(get<UserMongoProvider>().provideUserCollection(), get()) }
    single<TaskInProjectDataSource> { TaskInProjectMongoDataSource(get<TaskInProjectMongoProvider>().provideTaskInProjectCollection()) }
    single<MateTaskAssignmentDataSource> { MateTaskAssignmentMongoDataSource(get<MateTaskAssignmentMongoProvider>().provideMateTaskAssignmentCollection()) }
    single<UserAssignedToProjectDataSource> { UserAssignedToProjectMongoDataSource(get<UserAssignedToProjectMongoProvider>().provideUserAssignedToProjectCollection()) }
}
