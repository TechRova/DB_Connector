package in.techrova.dbconnectortwo.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.github.underscore.lodash.U;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import in.techrova.dbconnectortwo.MainActivityViewModel;
import in.techrova.dbconnectortwo.model.epoxymodels.BillItem;
import in.techrova.dbconnectortwo.utils.FBUtils;

public class ConnectionHelper {

    public final Connection conn;
    private final MainActivityViewModel viewModel;
    private final Context activity;
    TinyDB tinyDB;
    FBUtils fbUtils;

    public ConnectionHelper(MainActivityViewModel viewModel, Context applicationContext)
    {
        tinyDB = new TinyDB(applicationContext);
        this.viewModel = viewModel;
        this.activity = applicationContext;
        this.conn = createConnection();
        this.fbUtils = new FBUtils();
    }

    public Connection createConnection()
    {
        String _user = tinyDB.getString("dbUser"); // sa
        String _pass = tinyDB.getString("pass"); // admin123
        String _DB = tinyDB.getString("dbname"); // Amsa2021
        String _server = tinyDB.getString("server"); // 192.168.31.119

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";integrated security=False;";
            conn = DriverManager.getConnection(ConnURL);
            if (conn != null ) {
                Log.d("ConnHelp", "createConnection: connected "+conn.getMetaData().getDatabaseProductName());
                viewModel.success.postValue("connection success. \n \n Server name  "+conn.getMetaData().getDatabaseProductName());
            }
            else
            {
                viewModel.success.postValue("Can't connect");
            }
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
            viewModel.error.postValue(se);

        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
            viewModel.error.postValue(e);

        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
            viewModel.error.postValue(e);

        }
        return conn;
    }
    @SuppressLint("NewApi")
    public static Connection CONN(MutableLiveData<List<BillItem>> bills) {
        ZoneId zone = ZoneId.systemDefault();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("...pattern...").withZone(zone);
        df.format(Instant.ofEpochMilli(1245788));

        String _user = "sa";
        String _pass = "admin123";
        String _DB = "Amsa2021";
        String _server = "192.168.31.119";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";";
            conn = DriverManager.getConnection(ConnURL);
            if (conn != null) {
                Log.d("ConnectionHelper", "CONN: success");
                //sqlButton(conn,bills);
            }
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());

        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }

    public void sqlButton(MutableLiveData<List<BillItem>> bills,String date, int page){
        if (conn!=null){
            Statement statement = null;

            String queryFull = "DECLARE @PageSize INT,\n" +
                    "        @Page INT\n" +
                    "\n" +
                    "SELECT  @PageSize = 10,\n" +
                    "        @Page = "+page+" \n" +
                    "        \n" +
                    "        IF OBJECT_ID('tempdb..#tempSalHdr') IS NOT NULL\n" +
                    "        DROP TABLE #tempSalHdr\n" +
                    "        \n" +
                    "        Select *,\n" +
                    "                ROW_NUMBER() OVER(ORDER BY BillDt) ID\n" +
                    "       into #tempSalHdr FROM    SalHdr where BillDt = '"+date+"'  \n" +
                    "\n" +
                    ";WITH PageNumbers AS(\n" +
                    "        SELECT *\n" +
                    "        FROM    #tempSalHdr as PageNumbers where ID  BETWEEN ((@Page - 1) * @PageSize + 1)\n" +
                    "        AND (@Page * @PageSize)  \n" +
                    " )\n" +
                    "SELECT  *\n" +
                    "FROM    PageNumbers\n" +
                    "FOR XML AUTO,elements;";
            try {
             //   tinyDB.putString("query",queryFull);
                fbUtils.logEvent("query",queryFull);
                statement = conn.createStatement();
                Log.d("ConHelp", "sqlButton: "+date);
               // ResultSet resultSet = statement.executeQuery("Select * from SalHdr where BillDt = '"+date+"'  ORDER BY SalId OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY FOR JSON AUTO;");
                ResultSet resultSet = statement.executeQuery(queryFull);
                String xml = "<NewDataSet><DelProcess><SalId> 1 </SalId><Selection> 1 </Selection><BillNo> 1 </BillNo><BillDt> 2020-05-07 </BillDt><BillRefNo> 0000001 </BillRefNo><TotAmt> 276.13 </TotAmt><TaxAmt> 7 </TaxAmt><RndOff> 0.07 </RndOff><NetAmt> 290 </NetAmt><DiscAmt> 0 </DiscAmt><Discper> 0 </Discper></DelProcess></NewDataSet>";
                String json = "";
                while (resultSet.next())
                {
                    json += resultSet.getString(1);
                }
              //  while (resultSet.next()){
                    if (!TextUtils.isEmpty(json)) {
                        fbUtils.logEvent("xml",json);

                        Log.d("TAG", "sqlButton: " + json);
                        json = "<result>" + json + "</result>";
                        JSONObject jsonObject = new JSONObject(U.xmlToJson(json, U.Mode.REPLACE_SELF_CLOSING_WITH_NULL));
                        Log.d("TAG", "sqlButton: " + jsonObject.toString(2));
                        json = jsonObject.getJSONObject("result").getJSONArray("PageNumbers").toString(2);
                        Log.d("TAG", "sqlButton: " + json);
                        fbUtils.logEvent("json",json);

                        Type listType = new TypeToken<List<BillItem>>() {
                        }.getType();

                        List<BillItem> myModelList = new Gson().fromJson(json.trim(), listType);
                        Log.d("TAG", "json mdeol: " + myModelList.get(0).getBillDt());
                        fbUtils.logEvent("model date",myModelList.get(0).getBillDt());

                        // MutableLiveData<List<BillItem>> bills = new MutableLiveData<>();
                        bills.postValue(myModelList);
                    }
                    else
                    {
                        bills.postValue(null);
                    }


                //}
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
               viewModel.error.postValue(e);
            }
        }

    }

    public void deleteBills(String xml, MutableLiveData<List<BillItem>> bills , MutableLiveData<Integer> result , String date, int page) {

        if (conn!=null){
            Statement statement = null;
            try {
                statement = conn.createStatement();
               // String xml = "<NewDataSet><DelProcess><SalId> 1 </SalId><Selection> 1 </Selection><BillNo> 1 </BillNo><BillDt> 2020-05-07 </BillDt><BillRefNo> 0000001 </BillRefNo><TotAmt> 276.13 </TotAmt><TaxAmt> 7 </TaxAmt><RndOff> 0.07 </RndOff><NetAmt> 290 </NetAmt><DiscAmt> 0 </DiscAmt><Discper> 0 </Discper></DelProcess></NewDataSet>";
                int resultSet = statement.executeUpdate("exec DeleteProcessSP @DetStr = '" + xml + "',@CompId = 1");
                tinyDB.putString("query","exec DeleteProcessSP @DetStr = '" + xml + "',@CompId = 1");
                if (resultSet > 0)
                {
                    sqlButton(bills,date,page);
                    result.postValue(resultSet);
                }
                else
                {
                    Log.d("ConnHelper", "deleteBills: error ");
                    viewModel.success.postValue("failed  to delete " );


                }

            } catch (SQLException e) {
                Log.d("ConnHelper", "deleteBills: error ");
                viewModel.error.postValue(e);
                e.printStackTrace();
            }
        }
    }

}