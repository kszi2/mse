package hu.kszi2.mse.extension.reminder

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val parser = listOf(
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
    DateTimeFormatter.ISO_ZONED_DATE_TIME,
    DateTimeFormatter.RFC_1123_DATE_TIME
)

fun parse(datetimestring: String?): LocalDateTime? {
    for (par in parser) {
        return try {
            datetimestring.let { LocalDateTime.parse(datetimestring, par) }
        } catch (e: Exception) {
            continue
        }
    }
    throw Exception("Date error")
}