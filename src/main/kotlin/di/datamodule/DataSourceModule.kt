package di.datamodule

import logic.model.entities.*
import org.example.data.datasources.AuditSystemCsvDataSource
import org.example.data.datasources.UserCsvDataSource
import org.example.data.datasources.PlanMateDataSource
import org.example.data.datasources.ProjectCsvDataSource
import org.example.data.datasources.StateCsvDataSource
import org.example.data.datasources.TaskCsvDataSource
import org.koin.dsl.module

val datasourceModule = module {
    factory<PlanMateDataSource<AuditSystem>> { AuditSystemCsvDataSource() }
    factory<PlanMateDataSource<User>> { UserCsvDataSource(filePath = "users.csv") }
    factory<PlanMateDataSource<Project>> { ProjectCsvDataSource(filePath = "projects.csv") }
    factory<PlanMateDataSource<State>> { StateCsvDataSource() }
    factory<PlanMateDataSource<Task>> { TaskCsvDataSource() }
}