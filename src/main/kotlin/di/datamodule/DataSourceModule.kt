package di.datamodule

import org.example.data.datasources.models.audit_system_data_source.AuditSystemCsvDataSource
import data.datasources.relations.mate_task_assignment_data_source.MateTaskAssignmentCsvDataSource
import data.datasources.models.project_data_source.ProjectCsvDataSource
import org.example.data.datasources.models.audit_system_data_source.IAuditSystemDataSource
import org.example.data.datasources.models.project_data_source.IProjectDataSource
import org.example.data.datasources.models.state_data_source.IStateDataSource
import org.example.data.datasources.models.state_data_source.StateCsvDataSource
import org.example.data.datasources.models.task_data_source.ITaskDataSource
import org.example.data.datasources.models.task_data_source.TaskCsvDataSource
import org.example.data.datasources.models.user_data_source.UserCsvDataSource
import org.example.data.datasources.models.user_data_source.IUserDataSource
import org.example.data.datasources.relations.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.relations.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.datasources.relations.task_In_project_data_source.TaskInProjectCsvDataSource
import org.example.data.datasources.relations.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.datasources.relations.user_assigned_to_project_data_source.UserAssignedToProjectCsvDataSource
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
