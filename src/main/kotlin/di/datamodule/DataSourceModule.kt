package di.datamodule

import org.example.data.datasources.audit.AuditSystemCsvDataSource
import org.example.data.datasources.audit.AuditSystemDataSource
import org.example.data.datasources.authentication.AuthenticationCsvDataSource
import org.example.data.datasources.authentication.AuthenticationDataSource
import org.example.data.datasources.project.ProjectCsvDataSource
import org.example.data.datasources.project.ProjectDataSource
import org.example.data.datasources.state.StateCsvDataSource
import org.example.data.datasources.state.StateDataSource
import org.example.data.datasources.task.TaskCsvDataSource
import org.example.data.datasources.task.TaskDataSource
import org.koin.dsl.module

val datasourceModule = module {
    factory<AuditSystemDataSource> { AuditSystemCsvDataSource() }
    factory<AuthenticationDataSource> { AuthenticationCsvDataSource() }
    factory<ProjectDataSource> { ProjectCsvDataSource(get()) }
    factory<StateDataSource> { StateCsvDataSource() }
    factory<TaskDataSource> { TaskCsvDataSource() }
}