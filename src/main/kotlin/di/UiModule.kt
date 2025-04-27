package org.example.di

import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.task.admin.TaskManagerUiMateImp
import org.example.ui.features.task.mate.TaskManagerUiMateMateImp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory { AuditSystemManagerUi(get()) }
    factory { AuthenticationManagerUi(get()) }
    factory { ProjectManagerUi(get()) }
    factory { TaskManagerUiMateImp(get()) }
    factory { TaskManagerUiMateMateImp(get()) }
}