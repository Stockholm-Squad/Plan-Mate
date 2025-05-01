package di.datamodule

import logic.model.entities.*
import org.example.data.datasources.*
import org.example.data.entities.TaskInProject
import org.example.data.entities.UserAssignedToProject
import org.koin.dsl.module

val datasourceModule = module {
    factory<PlanMateDataSource<AuditSystem>> { AuditSystemCsvDataSource() }
    factory<PlanMateDataSource<User>> { AuthenticationCsvDataSource() }
    factory<PlanMateDataSource<Project>> { ProjectCsvDataSource(filePath = "projects.csv") }
    factory<PlanMateDataSource<TaskInProject>> { TaskInProjectCsvDataSource(filePath = "task_in_project.csv") }
    factory<PlanMateDataSource<UserAssignedToProject>> { UserAssignedToProjectCsvDataSource(filePath = "user_assigned_to_project.csv") }
    factory<PlanMateDataSource<State>> { StateCsvDataSource() }
    factory<PlanMateDataSource<Task>> { TaskCsvDataSource() }
}