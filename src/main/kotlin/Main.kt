package org.example

import logic.model.entities.Role
import logic.model.entities.User
import org.example.data.datasources.user_data_source.UserCsvDataSource
import org.example.di.appModule
import org.example.ui.PlanMateConsoleUi
import org.example.utils.hashToMd5
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.mp.KoinPlatform.getKoin

fun main() {
//    val dataSource = UserCsvDataSource("users.csv")
//    val adminUser = listOf(
//        User("rodina", hashToMd5("admin123"), Role.ADMIN),
//    )
//    val result = dataSource.overWrite(adminUser)
//    result.onSuccess { println("Appended tasks successfully") }
//        .onFailure { it.printStackTrace() }
//    dataSource.read().onSuccess { println("Current tasks:\n$it") }
//        .onFailure { it.printStackTrace() }
//

    startKoin {
        logger(PrintLogger())
        modules(appModule)
    }
    val planMateConsoleUi: PlanMateConsoleUi = getKoin().get()
    planMateConsoleUi.invoke()
}

