package hu.bme.aut.android.ring_me_up_app

import android.app.ProgressDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.ring_me_up_app.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    private val firebaseUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    protected val uid: String?
        get() = firebaseUser?.uid

    protected val userName: String?
        get() = firebaseUser?.displayName

    protected val userEmail: String?
        get() = firebaseUser?.email

    protected var userList = mutableListOf<User>()

    fun showProgressDialog() {
        if (progressDialog != null) {
            return
        }

        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading...")
            show()
        }
    }

    protected fun hideProgressDialog() {
        progressDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        progressDialog = null
    }

    protected fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun getUserByID(id: String?): User? {
        val db = Firebase.firestore
        var userData = User("", "", "", "")
        db.collection("users")
            .whereEqualTo("uid", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userData = document.toObject<User>()
                }
                if (documents.size() > 1) {
                    throw Exception("More than one user queried with this ID")
                }
            }

        return userData
    }

    protected fun getUserByName(userName: String?): User {
        val db = Firebase.firestore
        var userData = User("", "", "", "")
        db.collection("users")
            .whereEqualTo("userName", userName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userData = document.toObject<User>()
                }
                if (documents.size() > 1) {
                    throw Exception("More than one user queried with this ID")
                }
            }

        return userData
    }


    protected suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            val db = Firebase.firestore
            val _userList = db.collection("users")
                .get()
                .await()
                .documents
                .map{ document ->
                    document.toObject<User>()
                }
            _userList.filterNotNull()
        }
    }
}