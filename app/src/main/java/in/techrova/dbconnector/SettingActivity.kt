package `in`.techrova.dbconnector

import `in`.techrova.dbconnector.data.TinyDB
import `in`.techrova.dbconnector.utils.FBUtils
import android.R.attr.port
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress


class SettingActivity : AppCompatActivity() {

    lateinit var tinyDB: TinyDB
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        tinyDB = TinyDB(this)
        proceed.isEnabled = true
        FBUtils().logEvent("setting","loaded")
        initViews()
    }

    private fun initViews() {

        dbName.setText(tinyDB.getString("dbname"))
        dbUser.setText(tinyDB.getString("dbUser"))
        dbPass.setText(tinyDB.getString("pass"))
        serverIP.setText(tinyDB.getString("server"))

        proceed.setOnClickListener {
            var exists = false
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val sockaddr: SocketAddress =
                            InetSocketAddress(serverIP.text?.trim().toString(), 1433)
                        // Create an unbound socket
                        val sock = Socket()

                        // This method will block no more than timeoutMs.
                        // If the timeout occurs, SocketTimeoutException is thrown.
                        val timeoutMs = 2000 // 2 seconds
                        sock.connect(sockaddr, timeoutMs)
                        exists = true

                        runOnUiThread {
                            proceed.isEnabled = false

                            tinyDB.putString("dbname", dbName.text?.trim().toString())
                            tinyDB.putString("dbUser", dbUser.text?.trim().toString())
                            tinyDB.putString("pass", dbPass.text?.trim().toString())
                            tinyDB.putString("server", serverIP.text?.trim().toString())
                            FBUtils().logEvent("login",serverIP.text?.trim().toString())
                            Toast.makeText(this@SettingActivity, "Saved", Toast.LENGTH_LONG).show()

                            // startActivity(Intent(this@SettingActivity, MainActivity::class.java))
                            finish()
                        }

                    } catch (e: IOException) {
                        // Handle exception
                        runOnUiThread {
                            Toast.makeText(this@SettingActivity, "Server finding error : "+e.localizedMessage, Toast.LENGTH_LONG).show()
                            showAlert(e.localizedMessage)

                        }

                    }
                }
            }

        }
    }
    fun showAlert (msg: String)
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
    override fun onResume() {
        super.onResume()
        proceed.isEnabled = true
    }
}