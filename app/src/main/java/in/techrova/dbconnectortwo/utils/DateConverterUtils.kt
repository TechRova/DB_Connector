package `in`.techrova.dbconnectortwo.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateConverterUtils {
    companion object
    {
        @JvmStatic
        fun dateToEpoch(date: String, pattern: String) : Long
        {
            val localDate: LocalDate =
                LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))
            return localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        }

        @JvmStatic
        fun epochSecToDate(date: Long , pattern: String): String
        {
            val zone = ZoneId.systemDefault()
            val df = DateTimeFormatter.ofPattern("dd-MMM-yy").withZone(zone)
            return df.format(Instant.ofEpochSecond(date))
        }
    }
}