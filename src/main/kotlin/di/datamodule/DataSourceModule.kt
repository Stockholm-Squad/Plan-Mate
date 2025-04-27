import org.example.data.datasources.audit.AuditSystemCsvDataSourceImp
import org.example.data.datasources.audit.AuditSystemDataSource
import org.example.data.datasources.authentication.AuthenticationCsvDataSourceImp
import org.example.data.datasources.authentication.AuthenticationDataSource
import org.example.data.datasources.project.ProjectCsvDataSourceImp
import org.example.data.datasources.project.ProjectDataSource
import org.example.data.datasources.state.StateCsvDataSourceImp
import org.example.data.datasources.state.StateDataSource
import org.example.data.datasources.task.TaskCsvDataSourceImp
import org.example.data.datasources.task.TaskDataSource
import org.koin.dsl.module

val datasourceModule = module {
    factory<AuditSystemDataSource> { AuditSystemCsvDataSourceImp() }
    factory<AuthenticationDataSource> { AuthenticationCsvDataSourceImp() }
    factory<ProjectDataSource> { ProjectCsvDataSourceImp() }
    factory<StateDataSource> { StateCsvDataSourceImp() }
    factory<TaskDataSource> { TaskCsvDataSourceImp() }
}