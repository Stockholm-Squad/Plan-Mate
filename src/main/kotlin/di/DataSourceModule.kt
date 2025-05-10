package di

import org.example.data.datasources.*
import data.network.provider.AuditsMongoProvider
import data.network.provider.ProjectMongoProvider
import data.network.provider.StateMongoProvider
import data.network.provider.TaskMongoProvider
import data.network.provider.UserMongoProvider
import data.network.provider.TaskInProjectMongoProvider
import data.network.provider.MateTaskAssignmentMongoProvider
import data.network.provider.UserAssignedToProjectMongoProvider
import org.example.data.source.*
import org.example.data.source.local.*
import org.example.data.source.remote.*
import org.koin.dsl.module

val localDatasourceModule = module {
    factory<IAuditCSVReaderWriter> { AuditCSVReaderWriter(filePath = "audits.csv") }
    factory<IProjectCSVReaderWriter> { ProjectCSVReaderWriter(filePath = "projects.csv") }
    factory<IStateCSVReaderWriter> { StateCSVReaderWriter(filePath = "state.csv") }
    factory<ITaskCSVReaderWriter> { TaskCSVReaderWriter(filePath = "task.csv") }
    factory<IUserCSVReaderWriter> { UserCSVReaderWriter(filePath = "users.csv") }
    factory<ITaskInProjectCSVReaderWriter> { TaskInProjectCSVReaderWriter(filePath = "task_in_project.csv") }
    factory<IUserAssignedToProjectCSVReaderWriter> { UserAssignedToProjectCSVReaderWriter(filePath = "user_assigned_to_project.csv") }
    factory<IMateTaskAssignmentCSVReaderWriter> { MateTaskAssignmentCSVReaderWriter(filePath = "mate_task_assignment.csv") }
}

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
