package org.example

import org.example.di.appModule
import org.example.ui.PlanMateConsoleUi
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.mp.KoinPlatform.getKoin

fun main() {

    startKoin {
        logger(PrintLogger())
        modules(appModule)
    }
    val planMateConsoleUi: PlanMateConsoleUi = getKoin().get()
    planMateConsoleUi.invoke()
}