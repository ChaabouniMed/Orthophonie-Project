package org.cabinet.orthophonie.data.database

import android.content.Context
import androidx.compose.runtime.NoLiveLiterals
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.cabinet.orthophonie.database.AppDatabase

@NoLiveLiterals
actual class SqlDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "orthophonieapp.db")
    }
}