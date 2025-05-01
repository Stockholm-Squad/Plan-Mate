package org.example.di

import org.example.ui.utils.UiUtils
import org.example.utils.SearchUtils
import org.koin.dsl.module


val utilsModule = module {

    factory { UiUtils(get()) }
    factory { SearchUtils(get(), get()) }
}