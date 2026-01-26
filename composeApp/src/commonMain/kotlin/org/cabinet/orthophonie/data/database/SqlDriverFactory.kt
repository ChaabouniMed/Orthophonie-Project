package org.cabinet.orthophonie.data.database

import androidx.compose.runtime.NoLiveLiterals
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import org.cabinet.orthophonie.database.AppDatabase

@NoLiveLiterals
expect class SqlDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: SqlDriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(
        driver
    )
}