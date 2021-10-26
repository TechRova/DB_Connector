package `in`.techrova.dbconnectortwo

import `in`.techrova.dbconnectortwo.data.AppDatabase
import `in`.techrova.dbconnectortwo.data.DeletedBillDao
import `in`.techrova.dbconnectortwo.data.TinyDB
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_sumarry.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*


class SumarryActivity : AppCompatActivity() {
    private var delDao: DeletedBillDao? = null
    private var db: AppDatabase? = null
    private val viewModel  by viewModels<SummaryActivityViewModel> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sumarry)
        initViews()
        initRoom()
        initObservers()
    }

    private fun initViews() {

        date.setOnClickListener {
            SpinnerDatePickerDialogBuilder()
                .context(this@SumarryActivity)
                .spinnerTheme(R.style.NumberPickerStyle)

                .callback(DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    run {


                        date.text = "$dayOfMonth/${monthOfYear + 1}/$year"
                        viewModel.date = "$year/${monthOfYear + 1}/$dayOfMonth"
                        runBlocking {
                            withContext(Dispatchers.IO) {
                                viewModel.getDataSQL(delDao!!)

                            }
                        }
                    }
                })
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(
                        Calendar.MONTH
                    ), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
                .maxDate(2030, 0, 1)
                .minDate(2020, 0, 1)

                .build()
                .show()
        }

        query.setOnClickListener {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
// ...Irrelevant code for customizing the buttons and title
// ...Irrelevant code for customizing the buttons and title
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.query_text_lay, null)
            dialogBuilder.setView(dialogView)

            dialogView.findViewById<EditText>(R.id.query_ed).setText(TinyDB(this).getString("query"))
            val alertDialog: AlertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun initObservers() {
        viewModel.bills.observe(this, Observer {
            todayTotal.text = it[0].amount.toString()
        })

        viewModel.billsAll.observe(this, Observer {
            epoxyDel.withModels {
                if (it.isNotEmpty())
                it.forEach {
                    deleteBillItem {
                        id(it.date)
                        billItem(it)
                    }
                }

                else if (it.isNullOrEmpty())
                    noDataItem {
                        id(1007)
                        label("NO DATA FOUND")
                    }
            }

        })
    }

    private fun initRoom() {

        db = AppDatabase.getAppDataBase(context = this)
        delDao = db?.deletedDao()

     //   viewModel.getTodayEarning(delDao!!)
        viewModel.getAllEarnings(delDao!!)



    }
}