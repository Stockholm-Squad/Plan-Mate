package di.logicmodule

import org.example.logic.usecase.state.ManageStatesUseCase
import org.koin.dsl.module

val stateUseCaseModule = module {
    factory { ManageStatesUseCase(get()) }
}