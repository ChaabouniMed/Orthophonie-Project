package org.cabinet.orthophonie.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun String.parseToTime(): String =
    Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault()).format(
        LocalDateTime.Format {
            hour()
            char(':')
            minute()
        }
    )

fun String.parseToDate(): String =
    Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault()).format(
        LocalDateTime.Format {
            monthName(
                names = MonthNames.ENGLISH_FULL
            )
            char(' ')
            day(
                padding = Padding.NONE
            )
            char(',')
            char(' ')
            year()
        }
    )

fun Long.toInstant(): Instant = Instant.fromEpochMilliseconds(this)

fun Long.toLocalDateTime(): LocalDateTime = toInstant().toLocalDateTime(TimeZone.UTC)
