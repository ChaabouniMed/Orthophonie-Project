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

    /* ---------- INSERT ---------- */

    suspend fun insertPatient(patient: PatientRecord) =
        withContext(dispatchers.io) {
            queries.insertPatient(
                first_name = patient.first_name,
                last_name = patient.last_name,
                date_birth = patient.date_birth,
                contact_parent = patient.contact_parent,
                school = patient.school,
                status = patient.status,
                school_class = patient.school_class,
                creation_date = patient.creation_date
            )
        }

    /* ---------- UPDATE ---------- */

    suspend fun updatePatient(patient: PatientRecord) =
        withContext(dispatchers.io) {
            queries.updatePatient(
                first_name = patient.first_name,
                last_name = patient.last_name,
                date_birth = patient.date_birth,
                contact_parent = patient.contact_parent,
                school = patient.school,
                school_class = patient.school_class,
                status = patient.status,
                id = patient.id
            )
        }

    /* ---------- DELETE ---------- */

    suspend fun deletePatientById(id: Long) =
        withContext(dispatchers.io) {
            queries.deletePatientById(id)
        }

    /* ---------- GET ---------- */

    fun getPatients(): Query<PatientRecord> =
        queries.getPatients()

    fun getPatientById(id: Long): Query<PatientRecord> =
        queries.getPatientById(id)

    fun getActivePatients(): Query<PatientRecord> =
        queries.getActivePatients()

    fun getPatientsBySchool(school: String): Query<PatientRecord> =
        queries.getPatientsBySchool(school)

    fun getRecentPatients(limit: Long): Query<PatientRecord> =
        queries.getRecentPatients(limit)
}
