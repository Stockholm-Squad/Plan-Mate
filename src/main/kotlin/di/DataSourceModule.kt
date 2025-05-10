package di

import data.network.provider.AuditsMongoProvider
import data.network.provider.ProjectMongoProvider
import data.network.provider.StateMongoProvider
import data.network.provider.TaskMongoProvider
import data.network.provider.UserMongoProvider
import data.network.provider.TaskInProjectMongoProvider
import data.network.provider.MateTaskAssignmentMongoProvider
import data.network.provider.UserAssignedToProjectMongoProvider

import org.example.data.source.*
import org.example.data.source.remote.*
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
