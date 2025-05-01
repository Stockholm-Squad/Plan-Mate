package di.logicmodule

import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.koin.dsl.module


val auditSystemUseCaseModule = module {

    single { ManageAuditSystemUseCase(get()) }
}