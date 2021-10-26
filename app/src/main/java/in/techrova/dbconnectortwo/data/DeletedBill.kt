package `in`.techrova.dbconnectortwo.data

import `in`.techrova.dbconnectortwo.utils.DateConverterUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class DeletedBill(
    @PrimaryKey(autoGenerate = false)
    val date: Long,

    val amount: Int
)

{
    companion object
    {
        @JvmStatic
        fun String.toTimeslessDate(
            dateFormat: String = "yyyy/M/d", timeZone: TimeZone = TimeZone.getTimeZone(
                "UTC"
            )
        ): Date {
            val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
            parser.timeZone = timeZone
            return parser.parse(this)
        }

        @JvmStatic
        fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            formatter.timeZone = timeZone
            return formatter.format(this)
        }

        @JvmStatic
        @BindingAdapter("android:istTimelessDate")
        fun istTimelessDate(view: TextView, date: String)
        {
            view.text = date.toTimeslessDate().formatTo("dd MMM yy")
        }

        @JvmStatic
        @BindingAdapter("android:epochToDate")
        fun epochToDate(view: TextView, date: Long)
        {
           view.text= DateConverterUtils.epochSecToDate(date,"dd-MMM-yy")
        }
    }
}