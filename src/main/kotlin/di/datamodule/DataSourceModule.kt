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
    factory<PlanMateDataSource<AuditSystem>> { AuditSystemCsvDataSource(filePath = "audits.csv") }
    factory<PlanMateDataSource<User>> { AuthenticationCsvDataSource() }
    factory<PlanMateDataSource<TaskInProject>> { TaskInProjectCsvDataSource(filePath = "task_in_project.csv") }
    factory<PlanMateDataSource<Project>> { ProjectCsvDataSource(filePath = "projects.csv") }
    factory<PlanMateDataSource<State>> { StateCsvDataSource(filePath = "state.csv") }
    factory<PlanMateDataSource<Task>> { TaskCsvDataSource(filePath = "task.csv") }
    factory<PlanMateDataSource<MateTaskAssignment>> { MateTaskAssignmentCsvDataSource(filePath = "mate_task_assignment.csv") }
}