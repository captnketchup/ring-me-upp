package hu.bme.aut.android.ring_me_up_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.coroutineScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.ring_me_up_app.data.User
import hu.bme.aut.android.ring_me_up_app.databinding.ActivityMainBinding
import hu.bme.aut.android.ring_me_up_app.extensions.validateNonEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener { registerClick() }
        binding.btnLogin.setOnClickListener { loginClick() }
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun validateForm() =
        binding.etEmail.validateNonEmpty() && binding.etPassword.validateNonEmpty()

    private fun registerClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        var result = firebaseAuth
            .createUserWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
            .addOnSuccessListener { result ->
                hideProgressDialog()

                val firebaseUser = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(firebaseUser?.email?.substringBefore('@'))
                    .build()
                firebaseUser?.updateProfile(profileChangeRequest)

                toast("Registration successful")
                lifecycle.coroutineScope.launch {
                    val newUser = User(
                        uid,
                        binding.etEmail.text.toString().split("@")[0],
                        "0",
                        "0"
                    )
                    uploadNewUser(newUser)
                    Log.d("CICA", "uploadNewUser lefutott")
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.message)
            }


    }

    private fun loginClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .signInWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
            .addOnSuccessListener {
                hideProgressDialog()

                startActivity(Intent(this@MainActivity, DealsActivity::class.java))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                finish()
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.localizedMessage)
            }
    }

    private suspend fun uploadNewUser(user: User) {
        withContext(Dispatchers.IO) {
            val db = Firebase.firestore

            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    toast("User was added to db")
                }
            Log.d("CICA", "db feltoltes utani log")
        }
    }
}