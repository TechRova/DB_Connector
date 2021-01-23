package `in`.techrova.dbconnector

import `in`.techrova.dbconnector.data.ConnectionHelperKt
import `in`.techrova.dbconnector.model.epoxymodels.BillItem
import android.app.Application
import android.util.Log
import android.widget.CompoundButton
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

     var con: ConnectionHelperKt? = null
    lateinit var bills: MutableLiveData<MutableList<BillItem>>
    lateinit var total : MutableLiveData<Int>
    lateinit var result : MutableLiveData<Int>
    lateinit var progress : MutableLiveData<Boolean>
    lateinit var error : MutableLiveData<Throwable>
    lateinit var success : MutableLiveData<String>

    var page: Int = 1
    val delXML = mutableMapOf<Int,String>()
    var date: String = "${Calendar.getInstance().get(Calendar.YEAR )}/${Calendar.getInstance().get(Calendar.MONTH  )+ 1}/${Calendar.getInstance().get(Calendar.DAY_OF_MONTH )}"
    init {
        bills = MutableLiveData<MutableList<BillItem>>()
        total = MutableLiveData<Int>()
        result = MutableLiveData<Int>()
        progress = MutableLiveData<Boolean>()
        error = MutableLiveData<Throwable>()
        success = MutableLiveData<String>()
        total.postValue(0)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getDataSQL()
            }
        }

    }

   suspend fun getDataSQL() {
       // viewModelScope.launch {

            if (con == null) {
                con = ConnectionHelperKt(this@MainActivityViewModel,getApplication<Application>().applicationContext)
                con!!.sqlButton(bills, date,page)

            } else {
                con!!.sqlButton(bills, date,page)

            }
       // }
    }

    fun onCheckedChange(button: CompoundButton?, check: Boolean, billItem: BillItem) {
        Log.d("Z1D1", "onCheckedChange: $check ${billItem.billNo}")
        if (check) {
            total.postValue(total.value?.plus(billItem.netAmt))
            val xml = "<DelProcess><SalId> ${billItem.salId} </SalId><Selection> 1 </Selection> <BillNo> ${billItem.billNo} </BillNo> <BillDt> ${billItem.billDt} </BillDt><BillRefNo> ${billItem.billRefNo} </BillRefNo><TotAmt> ${billItem.totAmt} </TotAmt><TaxAmt> ${billItem.taxAmt} </TaxAmt><RndOff>${billItem.rndOff} </RndOff><NetAmt> ${billItem.netAmt} </NetAmt><DiscAmt> ${billItem.discAmt} </DiscAmt><Discper> ${billItem.discper}</Discper> </DelProcess>"
            Log.d(this.javaClass.simpleName, "delBill:  $xml")

            delXML[billItem.billNo] = xml
        }
        else {

            total.postValue(total.value?.minus(billItem.netAmt))
            delXML.remove(billItem.billNo)
        }


    }

     suspend fun delBill() {
        var queryProc = ""
         delXML.forEach {

             queryProc += it.value
             //Log.d(this.javaClass.simpleName, "delBill:  ${queryProc}")

         }
        queryProc = "<NewDataSet>$queryProc</NewDataSet>"
        Log.d(this.javaClass.simpleName, "delBill: ${queryProc}")
        con?.deleteBills(queryProc,bills,result,date,page)
    }

}