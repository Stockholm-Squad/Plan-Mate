package di.logicmodule

import org.example.logic.usecase.task.ManageTasksUseCase
import org.koin.dsl.module

val taskUseCaseModule = module {
    factory { ManageTasksUseCase(get()) }
}