package `in`.techrova.dbconnector.model.epoxymodels


import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class BillItem (

    @SerializedName("SalId") val salId : Int,
    @SerializedName("BillNo") val billNo : Int,
    @SerializedName("BillDt") val billDt : String,
    @SerializedName("BookId") val bookId : Int,
    @SerializedName("Cash") val cash : String,
    @SerializedName("BillRefNo") val billRefNo : Int,
    @SerializedName("PtyId") val ptyId : Int,
    @SerializedName("PtyName") val ptyName : Any? ,
    @SerializedName("Add1") val add1 : Any?,
    @SerializedName("City") val city : Any?,
    @SerializedName("TotAmt") val totAmt : Double,
    @SerializedName("TaxId") val taxId : Int,
    @SerializedName("Taxper") val taxper : Int,
    @SerializedName("TaxAmt") val taxAmt : Double,
    @SerializedName("RndOff") val rndOff : Double,
    @SerializedName("NetAmt") val netAmt : Int,
    @SerializedName("DiscAmt") val discAmt : Int,
    @SerializedName("Discper") val discper : Int,
    @SerializedName("Remarks") val remarks : Any?,
    @SerializedName("UserId") val userId : Int,
    @SerializedName("CompId") val compId : Int,
    @SerializedName("AcYrId") val acYrId : Int,
    @SerializedName("DocCancel") val docCancel : Boolean,
    @SerializedName("OtherState") val otherState : Boolean,
    @SerializedName("WaiterId") val waiterId : Int,
    @SerializedName("WaiterCode") val waiterCode : Int,
    @SerializedName("DeliveryId") val deliveryId : Int,
    @SerializedName("Delivery") val delivery : String,
    @SerializedName("Est") val est : Boolean,
    @SerializedName("BillTime") val billTime : String

)

{
    fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    fun getCorrectDate()
    {
        this.billDt.toDate().formatTo("dd MMM yyyy")
    }

    companion object {

        @JvmStatic
        @BindingAdapter("android:istDate")
        fun istDate (view: TextView, date: String)
        {
            view.text = date.toDate().formatTo("dd MMM yy")
        }

        fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
            val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
            parser.timeZone = timeZone
            return parser.parse(this.replace("T"," ",true))
        }

        fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            formatter.timeZone = timeZone
            return formatter.format(this)
        }

    }
}