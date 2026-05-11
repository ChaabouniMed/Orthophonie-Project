package org.cabinet.orthophonie.di

import org.cabinet.orthophonie.data.database.SqlDriverFactory
import org.cabinet.orthophonie.data.database.createDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dbModule: Module = module {
    factory { SqlDriverFactory() }
    single { createDatabase(driverFactory = get()) }
}