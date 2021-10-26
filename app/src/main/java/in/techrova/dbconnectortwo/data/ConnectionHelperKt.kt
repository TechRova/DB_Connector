package `in`.techrova.dbconnectortwo.data

import `in`.techrova.dbconnectortwo.MainActivityViewModel
import `in`.techrova.dbconnectortwo.model.epoxymodels.BillItem
import `in`.techrova.dbconnectortwo.utils.FBUtils
import android.annotation.SuppressLint
import android.content.Context
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.underscore.lodash.U
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement


class ConnectionHelperKt (viewModel: MainActivityViewModel, applicationContext: Context?) {
    val conn: Connection?
    private val viewModel: MainActivityViewModel
    private val activity: Context?
    var tinyDB: TinyDB
    var fbUtils: FBUtils

    fun createConnection(): Connection? {
        val _user = tinyDB.getString("dbUser") // sa
        val _pass = tinyDB.getString("pass") // admin123
        val _DB = tinyDB.getString("dbname") // Amsa2021
        val _server = tinyDB.getString("server") // 192.168.31.119
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn: Connection? = null
        var ConnURL: String? = null
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            ConnURL = ("jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";integrated security=False;")
            conn = DriverManager.getConnection(ConnURL)
            if (conn != null) {
                Log.d(
                    "ConnHelp",
                    "createConnection: connected " + conn.metaData.databaseProductName
                )
                viewModel.toast.postValue(
                    """connection success. 
 
 Server name  ${conn.metaData.databaseProductName}"""
                )
            } else {
                viewModel.success.postValue("Can't connect")
            }
        } catch (se: SQLException) {
            Log.e("ERRO", se.message)
            viewModel.error.postValue(se)
        } catch (e: ClassNotFoundException) {
            Log.e("ERRO", e.message)
            viewModel.error.postValue(e)
        } catch (e: Exception) {
            Log.e("ERRO", e.message)
            viewModel.error.postValue(e)
        }
        return conn
    }

    suspend fun sqlButton(bills: MutableLiveData<MutableList<BillItem>>, date: String, page: Int) {
        if (conn != null) {
            var statement: Statement? = null
            val queryFull = """DECLARE @PageSize INT,
        @Page INT

SELECT  @PageSize = 10,
        @Page = $page
        
        IF OBJECT_ID('tempdb..#tempSalHdr') IS NOT NULL
        DROP TABLE #tempSalHdr
        
        Select h.SalId,BillNo,BillDt,BillRefNo,Sum(Amount) as TotAmt,sum(d.TaxAmt)TaxAmt,DiscPer,Max(DiscAmt)DiscAmt,        
        Max(RndOff)RndOff,Round(Sum(Amount+d.TaxAmt),0) as NetAmt,
                ROW_NUMBER() OVER(ORDER BY BillDt) ID
       into #tempSalHdr FROM    SalHdr h,SalDet d         
Where h.SalId=d.SalId and ItemId not in (29,72) and BillDt = '$date'
Group by h.SalId,BillNo,BillDt,BillRefNo,h.DiscPer 

;WITH PageNumbers AS(
        SELECT *
        FROM    #tempSalHdr as PageNumbers where ID  BETWEEN ((@Page - 1) * @PageSize + 1)
        AND (@Page * @PageSize)  
 )
SELECT  *
FROM    PageNumbers
FOR XML AUTO,elements;"""
            try {
                //   tinyDB.putString("query",queryFull);
                fbUtils.logEvent("query", queryFull)
                statement = conn.createStatement()
                Log.d("ConHelp", "sqlButton: $date")
                // ResultSet resultSet = statement.executeQuery("Select * from SalHdr where BillDt = '"+date+"'  ORDER BY SalId OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY FOR JSON AUTO;");
                val resultSet = statement.executeQuery(queryFull)
                val xml =
                    "<NewDataSet><DelProcess><SalId> 1 </SalId><Selection> 1 </Selection><BillNo> 1 </BillNo><BillDt> 2020-05-07 </BillDt><BillRefNo> 0000001 </BillRefNo><TotAmt> 276.13 </TotAmt><TaxAmt> 7 </TaxAmt><RndOff> 0.07 </RndOff><NetAmt> 290 </NetAmt><DiscAmt> 0 </DiscAmt><Discper> 0 </Discper></DelProcess></NewDataSet>"
                var json = ""
                while (resultSet.next()) {
                    json += resultSet.getString(1)
                }
                //  while (resultSet.next()){
                if (!TextUtils.isEmpty(json)) {
                    fbUtils.logEvent("xml", json)
                    Log.d("TAG", "sqlButton: $json")
                    json = "<result>$json</result>"
                    val jsonObject =
                        JSONObject(U.xmlToJson(json, U.Mode.REPLACE_SELF_CLOSING_WITH_NULL))
                    Log.d("TAG", "sqlButton: " + jsonObject.toString(2))
                    json =
                        jsonObject.getJSONObject("result").getJSONArray("PageNumbers").toString(2)
                    Log.d("TAG", "sqlButton: $json")
                    fbUtils.logEvent("json", json)
                    val listType = object : TypeToken<List<BillItem?>?>() {}.type
                    val myModelList: List<BillItem> =
                        Gson().fromJson<List<BillItem>>(json.trim { it <= ' ' }, listType)
                    Log.d("TAG", "json mdeol: " + myModelList[0].billDt)
                    fbUtils.logEvent("model date", myModelList[0].billDt)

                    // MutableLiveData<List<BillItem>> bills = new MutableLiveData<>();
                    bills.postValue(myModelList as MutableList<BillItem>?)
                } else {
                    bills.postValue(null)
                }


                //}
            } catch (e: SQLException) {
                e.printStackTrace()
                viewModel.error.postValue(e)
            } catch (e: JSONException) {
                e.printStackTrace()
                viewModel.error.postValue(e)
            }
        }
    }

    suspend fun deleteBills(
        xml: String,
        bills: MutableLiveData<MutableList<BillItem>>,
        result: MutableLiveData<Int>,
        date: String,
        page: Int
    ) {
        if (conn != null) {
            var statement: Statement? = null
            try {
                statement = conn.createStatement()
                // String xml = "<NewDataSet><DelProcess><SalId> 1 </SalId><Selection> 1 </Selection><BillNo> 1 </BillNo><BillDt> 2020-05-07 </BillDt><BillRefNo> 0000001 </BillRefNo><TotAmt> 276.13 </TotAmt><TaxAmt> 7 </TaxAmt><RndOff> 0.07 </RndOff><NetAmt> 290 </NetAmt><DiscAmt> 0 </DiscAmt><Discper> 0 </Discper></DelProcess></NewDataSet>";
                val resultSet =
                    statement.executeUpdate("exec DeleteProcessSP @DetStr = '$xml',@CompId = 1")
                tinyDB.putString("query", "exec DeleteProcessSP @DetStr = '$xml',@CompId = 1")
                if (resultSet > 0) {
                    sqlButton(bills, date, page)
                    result.postValue(resultSet)
                } else {
                    Log.d("ConnHelper", "deleteBills: error result ${resultSet}")
                    viewModel.success.postValue("failed  to delete ")
                }
            } catch (e: SQLException) {
                Log.d("ConnHelper", "deleteBills: error ${e.message}")
                viewModel.error.postValue(e)
                e.printStackTrace()
            }
        }
    }

    companion object {
        @SuppressLint("NewApi")
        fun CONN(bills: MutableLiveData<List<BillItem?>?>?): Connection? {
            val _user = "sa"
            val _pass = "admin123"
            val _DB = "Amsa2021"
            val _server = "192.168.31.119"
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var conn: Connection? = null
            var ConnURL: String? = null
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                ConnURL = ("jdbc:jtds:sqlserver://" + _server + ";"
                        + "databaseName=" + _DB + ";user=" + _user + ";password="
                        + _pass + ";")
                conn = DriverManager.getConnection(ConnURL)
                if (conn != null) {
                    Log.d("ConnectionHelper", "CONN: success")
                    //sqlButton(conn,bills);
                }
            } catch (se: SQLException) {
                Log.e("ERRO", se.message)
            } catch (e: ClassNotFoundException) {
                Log.e("ERRO", e.message)
            } catch (e: Exception) {
                Log.e("ERRO", e.message)
            }
            return conn
        }
    }

    init {
        tinyDB = TinyDB(applicationContext)
        this.viewModel = viewModel
        activity = applicationContext
        conn = createConnection()
        fbUtils = FBUtils()
    }
}