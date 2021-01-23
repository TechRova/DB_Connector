package `in`.techrova.dbconnector

import `in`.techrova.dbconnector.data.AppDatabase
import `in`.techrova.dbconnector.data.DeletedBill
import `in`.techrova.dbconnector.data.DeletedBillDao
import `in`.techrova.dbconnector.model.epoxymodels.BillItem
import `in`.techrova.dbconnector.utils.DateConverterUtils
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var delDao: DeletedBillDao? = null
    private var db: AppDatabase? = null
    var data = ArrayList<BillItem> ()
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val viewModel  by viewModels<MainActivityViewModel> ()
    var progressDialog : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate: ");
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("loading")
        if (!progressDialog!!.isShowing) progressDialog?.show()
        buildDataList()
        initViews()
        initRoom()

    }

    private fun initRoom() {

        db = AppDatabase.getAppDataBase(context = this)
        delDao = db?.deletedDao()

    }

    private fun initViews() {
        summary.setOnClickListener {
            startActivity(Intent(this, SumarryActivity::class.java))
        }
        delbtn.setOnClickListener {


            AlertDialog.Builder(this)
                .setTitle("Beware")
                .setMessage("Do you  want to delete selected ${viewModel.delXML.size} items?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(
                    "DELETE"
                ) { dialog, whichButton ->
                    if (!progressDialog!!.isShowing) progressDialog?.show()
/*                    runBlocking {
                        withContext(Dispatchers.IO) {
                            viewModel.delBill()
                        }}*/
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            viewModel.delBill()
                        }
                    }

                    dialog.dismiss()

                }
                .setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int ->
                    if (progressDialog!!.isShowing) progressDialog?.dismiss()
                    dialogInterface.dismiss()
                }.show()
        }
        date.text = "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}/${Calendar.getInstance().get(
            Calendar.MONTH
        ) + 1}/${Calendar.getInstance().get(Calendar.YEAR)}"
        date.setOnClickListener {
            SpinnerDatePickerDialogBuilder()
                .context(this@MainActivity)
                .spinnerTheme(R.style.NumberPickerStyle)

                .callback(DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    run {
                        viewModel.progress.postValue(true)

                        date.text = "$dayOfMonth/${monthOfYear + 1}/$year"
                        viewModel.page = 1
                        pageNo.text = "1"
                        viewModel.date = "$year/${monthOfYear + 1}/$dayOfMonth"
                        viewModel.total.postValue(0)
                        viewModel.delXML.clear()
/*                        runBlocking {
                            withContext(Dispatchers.IO) {
                                viewModel.getDataSQL()

                            }
                        }*/
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                viewModel.getDataSQL()
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

        btnPageUp.setOnClickListener {
           // viewModel.progress.postValue(true)
            if (!progressDialog!!.isShowing) progressDialog?.show()
            viewModel.total.postValue(0)
            viewModel.delXML.clear()
            viewModel.page++
            pageNo.text = viewModel.page.toString()
/*            runBlocking {
                withContext(Dispatchers.IO) {
                    viewModel.getDataSQL()
                }
            }*/
            scope.launch {
                withContext(Dispatchers.IO) {
                    viewModel.getDataSQL()
                }
            }
        }

        viewModel.progress.observe(this, Observer {

            progressDialog?.setTitle("Loading");
            if (it) {
                progressDialog?.show()
            } else {
                if (progressDialog!!.isShowing) progressDialog?.dismiss()
            }
        })

        btnPageDown.setOnClickListener {
            if (viewModel.page == 1) return@setOnClickListener
            viewModel.page--
            pageNo.text = viewModel.page.toString()
           // viewModel.progress.postValue(true)
            viewModel.total.postValue(0)
            viewModel.delXML.clear()
            if (!progressDialog!!.isShowing) progressDialog?.show()
/*            runBlocking {
                withContext(Dispatchers.IO) {
                    viewModel.getDataSQL()

                }
            }*/
            scope.launch {
                withContext(Dispatchers.IO) {
                    viewModel.getDataSQL()
                }
            }
        }

        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            viewModel.progress.postValue(false)
            showAlert(it.localizedMessage)

        })

        viewModel.success.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()

            showAlert(it)
            viewModel.progress.postValue(false)

        })
    }

    fun showAlert(msg: String)
    {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton(
            getString(android.R.string.ok)
        ) { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun buildDataList() {
        viewModel.bills.observe(this, Observer {
            viewModel.progress.postValue(false)
            if (it == null) {
                Toast.makeText(this, "No data available for selected date", Toast.LENGTH_LONG)
                    .show()

                epoxy.clearPreloaders()
                epoxy.clear()

                epoxy.withModels {
                    noDataItem {
                        id(1008)
                        label("No Data Found")
                    }
                }

                return@Observer

            }
            epoxy.clearPreloaders()
            epoxy.clear()

            epoxy.withModels {


                it.forEach {
                    Log.d(this.javaClass.simpleName, "buildDataList: ${it.netAmt}")
                    billItem {
                        id(it.billNo)
                        billItem(it)
                        viewmodel(viewModel)

                    }
                }


            }
            Toast.makeText(this, "Scroll list down for more", Toast.LENGTH_LONG).show()


        })

        viewModel.total.observe(this, Observer {
            total.text = it.toString()
        })

        viewModel.result.observe(this, Observer {
            runBlocking {
                withContext(Dispatchers.IO) {
                    if (it > 0) {
                        val localDate: LocalDate =
                            LocalDate.parse(viewModel.date, DateTimeFormatter.ofPattern("yyyy/M/d"))
                        Log.d(this@MainActivity.javaClass.simpleName, "buildDataList: dateepoch ${localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()}")

                        val deletedBill = delDao?.getGenderByName(DateConverterUtils.dateToEpoch(viewModel.date,"yyyy/M/d"))
                        if (deletedBill != null && deletedBill.isNotEmpty()) {
                            Log.d("ROOM", "buildDataList: updating")

                            delDao?.updateGender(
                                DeletedBill(
                                    date = DateConverterUtils.dateToEpoch(viewModel.date,"yyyy/M/d"),
                                    amount = deletedBill[0].amount + viewModel.total.value!!
                                )
                            )
                        } else {
                            Log.d("ROOM", "buildDataList: insert")

                            delDao?.insertGender(
                                DeletedBill(
                                    date = DateConverterUtils.dateToEpoch(viewModel.date,"yyyy/M/d"),
                                    amount = viewModel.total.value!!
                                )
                            )

                        }
                        viewModel.total.postValue(0)
                        viewModel.delXML.clear()
                    }
                }

            }
        })



    }

/*    inner class ConDB : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val con = ConnectionHelper()
            val connect: Connection = ConnectionHelper.CONN(bills)
            return null
        }*/
}




