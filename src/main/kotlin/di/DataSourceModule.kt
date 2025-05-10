package di.datamodule

import org.example.data.source.local.AuditSystemCsvDataSource
import org.example.data.datasources.IAuditSystemDataSource
import org.example.data.datasources.IMateTaskAssignmentDataSource
import org.example.data.source.local.MateTaskAssignmentCsvDataSource
import org.example.data.datasources.IProjectDataSource
import org.example.data.source.local.ProjectCsvDataSource
import org.example.data.datasources.IStateDataSource
import org.example.data.source.local.StateCsvDataSource
import org.example.data.datasources.ITaskInProjectDataSource
import org.example.data.source.local.TaskInProjectCsvDataSource
import org.example.data.datasources.ITaskDataSource
import org.example.data.source.local.TaskCsvDataSource
import org.example.data.datasources.IUserAssignedToProjectDataSource
import org.example.data.source.local.UserAssignedToProjectCsvDataSource
import org.example.data.datasources.IUserDataSource
import org.example.data.source.local.UserCsvDataSource
import org.koin.dsl.module

val datasourceModule = module {
    factory<IAuditSystemDataSource> { AuditSystemCsvDataSource(filePath = "audits.csv") }
    factory<IProjectDataSource> { ProjectCsvDataSource(filePath = "projects.csv") }
    factory<IStateDataSource> { StateCsvDataSource(filePath = "state.csv") }
    factory<ITaskDataSource> { TaskCsvDataSource(filePath = "task.csv") }
    factory<IUserDataSource> { UserCsvDataSource(filePath = "users.csv") }
    factory<ITaskInProjectDataSource> { TaskInProjectCsvDataSource(filePath = "task_in_project.csv") }
    factory<IUserAssignedToProjectDataSource> { UserAssignedToProjectCsvDataSource(filePath = "user_assigned_to_project.csv") }
    factory<IMateTaskAssignmentDataSource> { MateTaskAssignmentCsvDataSource(filePath = "mate_task_assignment.csv") }
}
