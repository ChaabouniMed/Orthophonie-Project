package org.cabinet.orthophonie.data.database

import androidx.compose.runtime.NoLiveLiterals
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import org.cabinet.orthophonie.database.AppDatabase
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.database.SessionRecord

@NoLiveLiterals
expect class SqlDriverFactory {
    fun createDriver(): SqlDriver
}

val intAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

fun createDatabase(driverFactory: SqlDriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(
        driver,
        sessionRecordAdapter = SessionRecord.Adapter(
            duration_minutesAdapter = intAdapter,
            session_typeAdapter = EnumColumnAdapter(),
            attendance_statusAdapter = EnumColumnAdapter(),
        ),
        patientRecordAdapter = PatientRecord.Adapter(
            statusAdapter = EnumColumnAdapter()
        )
    )
}