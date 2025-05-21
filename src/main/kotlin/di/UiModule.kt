package org.example.di

import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.addusertoproject.AddUserToProjectUI
import org.example.ui.features.audit.AuditManagerUI
import org.example.ui.features.common.utils.UiUtils
import org.example.ui.features.login.LoginUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.EntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.CreateUserUi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory<AuditManagerUI> { AuditManagerUI(get(), get(), get(), get()) }
    factory<LoginUi> { LoginUi(get(), get(), get()) }
    factory<CreateUserUi> { CreateUserUi(get(), get(), get()) }
    factory<ProjectManagerUi> { ProjectManagerUi(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory<AddUserToProjectUI> { AddUserToProjectUI(get(), get(), get(), get(), get()) }
    factory<ShowAllEntityStateManagerUi> { ShowAllEntityStateManagerUi(get(), get()) }
    factory<AdminEntityStateManagerUi> { AdminEntityStateManagerUi(get(), get(), get(), get(), get()) }
    factory<TaskManagerUi> { TaskManagerUi(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory<EntityStateManagerUi> { EntityStateManagerUi(get(), get(), get(), get()) }
    factory<UiUtils> { UiUtils() }
}
