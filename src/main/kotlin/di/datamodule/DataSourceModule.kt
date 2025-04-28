package di.datamodule

import logic.model.entities.*
import org.example.data.datasources.AuditSystemCsvDataSource
import org.example.data.datasources.AuthenticationCsvDataSource
import org.example.data.datasources.PlanMateDataSource
import org.example.data.datasources.CsvDataSource
import org.example.data.datasources.StateCsvDataSource
import org.example.data.datasources.TaskCsvDataSource
import org.koin.dsl.module

val datasourceModule = module {
    factory<PlanMateDataSource<AuditSystem>> { AuditSystemCsvDataSource() }
    factory<PlanMateDataSource<User>> { AuthenticationCsvDataSource() }
    factory<PlanMateDataSource<Project>> { CsvDataSource<Project>(filePath = "projects.csv") }
    factory<PlanMateDataSource<State>> { StateCsvDataSource() }
    factory<PlanMateDataSource<Task>> { TaskCsvDataSource() }
}