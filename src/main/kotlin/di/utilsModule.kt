package org.example.di

import org.example.utils.SearchUtils
import org.koin.dsl.module

val utilsModule = module {
    factory<SearchUtils> { SearchUtils(get(), get()) }
}