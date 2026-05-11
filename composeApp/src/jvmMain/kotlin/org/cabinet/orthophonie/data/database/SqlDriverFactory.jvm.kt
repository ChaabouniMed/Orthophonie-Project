package org.cabinet.orthophonie.data.database

import androidx.compose.runtime.NoLiveLiterals
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.cabinet.orthophonie.database.AppDatabase
import java.io.File

@NoLiveLiterals
actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseFile = File(System.getProperty("user.home"), "orthophonie.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        
        if (!databaseFile.exists()) {
            AppDatabase.Schema.create(driver)
        }
        
        return driver
    }
}