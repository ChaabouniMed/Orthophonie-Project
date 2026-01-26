package org.cabinet.orthophonie.data.patients

import app.cash.sqldelight.Query
import kotlinx.coroutines.withContext
import org.cabinet.orthophonie.database.PatientQueries
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.utils.AppDispatchers

class PatientDao(
    private val dispatchers: AppDispatchers,
    private val queries: PatientQueries
) {
    suspend fun insertPatient(clerk: PatientRecord) = withContext(dispatchers.io) {
        queries.insertPatient(clerk)
    }

    fun getPatients(): Query<PatientRecord> = queries.getPatients()
}