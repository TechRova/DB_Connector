package `in`.techrova.dbconnector.utils

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBUtils {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val database = Firebase.database
    val myRef: DatabaseReference = database.getReference("logd")

    public fun logEvent(title: String, data: String) {
        Log.d(this@FBUtils.javaClass.simpleName, "logevent: ")
        myRef.child(title).setValue(data)
            .addOnSuccessListener {
                Log.d(this@FBUtils.javaClass.simpleName, "logevent: fb compl")

            }
            .addOnFailureListener {
                Log.d(
                    this@FBUtils.javaClass.simpleName,
                    "logevent: fb fall ${it.localizedMessage} \n ${it.cause}"
                )

            }
            .addOnCompleteListener {
                if (it.isSuccessful) Log.d(this@FBUtils.javaClass.simpleName, "logevent: fb suc")
                else Log.d(
                    this@FBUtils.javaClass.simpleName,
                    "logevent: fb not compl ${it.exception?.message}"
                )


            }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, 1001)
            param(FirebaseAnalytics.Param.ITEM_NAME, title)
            param(FirebaseAnalytics.Param.CONTENT, data)
        }

    }

}