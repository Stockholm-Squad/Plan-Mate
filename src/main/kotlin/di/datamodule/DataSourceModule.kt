package di.datamodule

import logic.model.entities.*
import org.example.data.datasources.AuditSystemCsvDataSource
import org.example.data.datasources.AuthenticationCsvDataSource
import org.example.data.datasources.MateTaskAssignmentCsvDataSource
import org.example.data.datasources.PlanMateDataSource
import org.example.data.datasources.ProjectCsvDataSource
import org.example.data.datasources.StateCsvDataSource
import org.example.data.datasources.TaskCsvDataSource
import org.example.data.datasources.TaskInProjectCsvDataSource
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.koin.dsl.module

val datasourceModule = module {
    factory<PlanMateDataSource<AuditSystem>> { AuditSystemCsvDataSource() }
    factory<PlanMateDataSource<User>> { AuthenticationCsvDataSource() }
    factory<PlanMateDataSource<Project>> { ProjectCsvDataSource(filePath = "projects.csv") }
    factory<PlanMateDataSource<State>> { StateCsvDataSource() }
    factory<PlanMateDataSource<Task>> { TaskCsvDataSource(filePath = "tasks.csv") }
    factory<PlanMateDataSource<TaskInProject>> { TaskInProjectCsvDataSource(filePath = "tasks_in_project") }
    factory<PlanMateDataSource<MateTaskAssignment>> { MateTaskAssignmentCsvDataSource(filePath = "mate_task_assignment") }
}