package org.example.di

import org.example.ui.utils.UiUtils
import org.koin.dsl.module


val utilsModule = module {

    factory { UiUtils(get()) }
}