package `in`.techrova.dbconnector

import `in`.techrova.dbconnector.model.epoxymodels.BillItem
import android.util.Log
import com.github.underscore.lodash.U
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var json = "<result><PageNumbers><SalId>47027</SalId><BillNo>47027</BillNo><BillDt>2020-10-09T00:00:00</BillDt><BookId>1</BookId><Cash>CA</Cash><BillRefNo>0047027</BillRefNo><PtyId>0</PtyId><PtyName></PtyName><Add1></Add1><City></City><TotAmt>133.33</TotAmt><TaxId>7</TaxId><Taxper>0.00</Taxper><TaxAmt>6.66</TaxAmt><RndOff>0.01</RndOff><NetAmt>140.00</NetAmt><DiscAmt>0.00</DiscAmt><Discper>0.00</Discper><Remarks></Remarks><UserId>2</UserId><CompId>1</CompId><AcYrId>12</AcYrId><DocCancel>0</DocCancel><OtherState>0</OtherState><WaiterId>9</WaiterId><WaiterCode>9</WaiterCode><DeliveryId>0</DeliveryId><Delivery>PARCEL</Delivery><Est>0</Est><BillTime>2020-10-09T09:07:18</BillTime><ID>1</ID></PageNumbers><PageNumbers><SalId>47028</SalId><BillNo>47028</BillNo><BillDt>2020-10-09T00:00:00</BillDt><BookId>1</BookId><Cash>CA</Cash><BillRefNo>0047028</BillRefNo><PtyId>0</PtyId><PtyName></PtyName><Add1></Add1><City></City><TotAmt>304.75</TotAmt><TaxId>7</TaxId><Taxper>0.00</Taxper><TaxAmt>15.24</TaxAmt><RndOff>0.01</RndOff><NetAmt>320.00</NetAmt><DiscAmt>0.00</DiscAmt><Discper>0.00</Discper><Remarks></Remarks><UserId>2</UserId><CompId>1</CompId><AcYrId>12</AcYrId><DocCancel>0</DocCancel><OtherState>0</OtherState><WaiterId>9</WaiterId><WaiterCode>9</WaiterCode><DeliveryId>0</DeliveryId><Delivery>PARCEL</Delivery><Est>0</Est><BillTime>2020-10-09T09:07:49</BillTime><ID>2</ID></PageNumbers></result>";
        print(U.xmlToJson(json))
        val jsonObject = JSONObject(U.xmlToJson(json))
        json = jsonObject.toString(2)
        Log.d("TAG", "sqlButton: $json")

        val listType = object : TypeToken<List<BillItem?>?>() {}.type

        val myModelList: List<BillItem> =
            Gson().fromJson<List<BillItem>>(json.trim { it <= ' ' }, listType)
        Log.d("TAG", "json mdeol: " + myModelList[0].billDt)
        assertEquals(4, 2 + 2)
    }
}