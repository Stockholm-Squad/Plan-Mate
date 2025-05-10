package di

import data.source.remote.mongo.*
import org.example.data.source.*
import org.example.data.source.remote.provider.*
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single<AuditDataSource> { AuditMongoDataSource(get<AuditsMongoProvider>().provideAuditsCollection()) }
    single<ProjectDataSource> { ProjectMongoDataSource(get<ProjectMongoProvider>().provideProjectCollection(), get()) }
    single<TaskDataSource> { TaskMongoDataSource(get<TaskMongoProvider>().provideTaskCollection()) }
    single<EntityStateDataSource> { EntityStateMongoDataSource(get<EntityStateMongoProvider>().provideEntityStatesCollection()) }
    single<UserDataSource> { UserMongoDataSource(get<UserMongoProvider>().provideUserCollection(), get()) }
    single<TaskInProjectDataSource> { TaskInProjectMongoDataSource(get<TaskInProjectMongoProvider>().provideTaskInProjectCollection()) }
    single<MateTaskAssignmentDataSource> { MateTaskAssignmentMongoDataSource(get<MateTaskAssignmentMongoProvider>().provideMateTaskAssignmentCollection()) }
    single<UserAssignedToProjectDataSource> { UserAssignedToProjectMongoDataSource(get<UserAssignedToProjectMongoProvider>().provideUserAssignedToProjectCollection()) }
}
