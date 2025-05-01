package di.datamodule

import logic.model.entities.*
import org.example.data.datasources.*
import org.koin.dsl.module

val datasourceModule = module {
    factory<PlanMateDataSource<AuditSystem>> { AuditSystemCsvDataSource(filePath = "audits.csv") }
    factory<PlanMateDataSource<User>> { UserCsvDataSource(filePath = "user.csv") }
    factory<PlanMateDataSource<Project>> { ProjectCsvDataSource(filePath = "projects.csv") }
    factory<PlanMateDataSource<State>> { StateCsvDataSource(filePath = "state.csv") }
    factory<PlanMateDataSource<Task>> { TaskCsvDataSource() }
}