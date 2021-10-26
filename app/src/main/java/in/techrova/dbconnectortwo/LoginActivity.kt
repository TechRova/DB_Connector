package `in`.techrova.dbconnectortwo

import `in`.techrova.dbconnectortwo.data.TinyDB
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var tinyDB: TinyDB
    private lateinit var database : FirebaseDatabase

    lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        database = Firebase.database
        myRef = database.getReference("logd")
        tinyDB = TinyDB(this)
        initViews()
    }

    private fun initViews() {

        settings.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SettingActivity::class.java))
        }
        proceed.setOnClickListener {
            if (loguser.text.toString() == "avb" && pass.text.toString() == "avb")
            {
                logEvent("login","main login")
                if (checkSetting())
                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                else
                    Toast.makeText(this, "Please enter settings using above button", Toast.LENGTH_LONG).show()

            }
            else
                Toast.makeText(this, "Wrong details", Toast.LENGTH_LONG).show()

        }
    }

    fun logEvent(title: String, data: String) {
        Log.d(this@LoginActivity.javaClass.simpleName, "logevent: ")
        myRef.child(title).setValue(data)
            .addOnSuccessListener {
                Log.d(this@LoginActivity.javaClass.simpleName, "logevent: fb compl")

            }
            .addOnFailureListener {
                Log.d(
                    this@LoginActivity.javaClass.simpleName,
                    "logevent: fb fall ${it.localizedMessage} \n ${it.cause}"
                )

            }
            .addOnCompleteListener {
                if (it.isSuccessful) Log.d(this@LoginActivity.javaClass.simpleName, "logevent: fb suc")
                else Log.d(
                    this@LoginActivity.javaClass.simpleName,
                    "logevent: fb not compl ${it.exception?.message}"
                )


            }



    }

    fun checkSetting() : Boolean
    {
        return !TextUtils.isEmpty(tinyDB.getString("dbname")) && !TextUtils.isEmpty(tinyDB.getString("dbUser")) && !TextUtils.isEmpty(tinyDB.getString("pass")) && !TextUtils.isEmpty(tinyDB.getString("server"))
    }
}