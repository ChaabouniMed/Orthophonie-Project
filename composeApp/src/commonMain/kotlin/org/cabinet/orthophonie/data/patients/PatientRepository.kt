package org.cabinet.orthophonie.data.patients

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.cabinet.orthophonie.database.PatientRecord
import org.cabinet.orthophonie.utils.AppDispatchers

class PatientRepository(
    private val dao: PatientDao,
    private val dispatchers: AppDispatchers
) {
    val patients: Flow<List<PatientRecord>>
        get() = dao.getPatients().asFlow().mapToList(dispatchers.io).distinctUntilChanged()
}