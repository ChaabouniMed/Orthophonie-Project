package org.cabinet.orthophonie.di

import org.cabinet.orthophonie.data.database.SqlDriverFactory
import org.cabinet.orthophonie.data.database.createDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dbModule: Module
    get() = module {
        factory { SqlDriverFactory(androidContext()) }
        single { createDatabase(driverFactory = get()) }
    }