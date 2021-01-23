package `in`.techrova.dbconnector

import `in`.techrova.dbconnector.data.DeletedBill
import `in`.techrova.dbconnector.data.DeletedBillDao
import `in`.techrova.dbconnector.model.epoxymodels.BillItem
import `in`.techrova.dbconnector.utils.DateConverterUtils
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

public class SummaryActivityViewModel(application: Application) : AndroidViewModel(application)
{
    var date: String = "${Calendar.getInstance().get(Calendar.YEAR )}/${
        Calendar.getInstance().get(
            Calendar.MONTH  )+ 1} / ${Calendar.getInstance().get(Calendar.DAY_OF_MONTH )}"

    lateinit var bills: MutableLiveData<List<DeletedBill>>
    lateinit var todayBills: MutableLiveData<List<DeletedBill>>
    lateinit var billsAll: MutableLiveData<List<DeletedBill>>


    fun getTodayEarning (delDao: DeletedBillDao)
    {
        bills = MutableLiveData<List<DeletedBill>>()
        billsAll = MutableLiveData<List<DeletedBill>>()
        runBlocking {
            withContext(Dispatchers.IO) {
                val deletedBill = delDao?.getGenderByName(1001)
                if (deletedBill != null && deletedBill.isNotEmpty()) {
                    bills.postValue(deletedBill)
                }
            }
        }

    }

    fun getAllEarnings (delDao: DeletedBillDao)
    {

        bills = MutableLiveData<List<DeletedBill>>()
        billsAll = MutableLiveData<List<DeletedBill>>()
        runBlocking {
            withContext(Dispatchers.IO) {
                val deletedBill = delDao?.getGenders()
                if (deletedBill != null && deletedBill.isNotEmpty()) {
                    billsAll.postValue(deletedBill)
                }
            }
        }

    }
    fun getDataSQL(delDao: DeletedBillDao) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val deletedBill = delDao?.getGenderByName(DateConverterUtils.dateToEpoch(date,"yyyy/M/d"))
                if (deletedBill != null ) {
                    billsAll.postValue(deletedBill)
                }
            }
        }
    }

}