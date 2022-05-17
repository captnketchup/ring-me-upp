package hu.bme.aut.android.ring_me_up_app

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.ring_me_up_app.data.Deal
import hu.bme.aut.android.ring_me_up_app.data.User
import hu.bme.aut.android.ring_me_up_app.databinding.ActivityCreateDealBinding
import hu.bme.aut.android.ring_me_up_app.extensions.validateNonEmpty
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class CreateDealActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        private const val REQUEST_CODE = 101
    }

    private lateinit var binding: ActivityCreateDealBinding
    private lateinit var debtorName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDealBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.btnSend.setOnClickListener { sendClick() }

        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userList = getAllUsers() as MutableList<User>
                updateSpinner()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        debtorName = parent.getItemAtPosition(pos) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    private fun sendClick() {
        if (!validateForm()) {
            return
        }

        uploadDeal()
    }

    private fun validateForm() =
        binding.etDebtSum.validateNonEmpty() && binding.etItem.validateNonEmpty()

    private fun uploadDeal() {
        val newDeal = Deal(
            uid,
            userName,
            debtorName,
            binding.etDebtSum.text.toString() + " Ft",
            binding.etItem.text.toString(),
            "${Calendar.getInstance().get(Calendar.YEAR)}.${
                Calendar.getInstance()
                    .get(Calendar.MONTH) + 1      // for whatever reason Java Calendar is one less 
            }.${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}",
            (0..999999999).random()
        )

        val db = Firebase.firestore

        db.collection("deals")
            .add(newDeal)
            .addOnSuccessListener {
                toast("Deal was created")

//                lifecycle.coroutineScope.launch {
//                    adjustBalance(newDeal)
//                }

                finish()
            }
            .addOnFailureListener { e -> toast(e.toString()) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
    }

    private fun updateSpinner() {
        val userArrayForSpinner = userList
            .map { user ->
                user.userName
            }
            .filter {
                it != userName
            }


        val spinner: Spinner = findViewById(R.id.users_spinner)
        spinner.onItemSelectedListener = this
        ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            userArrayForSpinner
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
        }
    }

    private suspend fun adjustBalance(deal: Deal) {
        withContext(Dispatchers.IO) {
            val db = Firebase.firestore

            /**
             * adjust debtor balance -
             */
            var authorList = db.collection("users")
                .get()
                .await()
                .documents
                .map { document ->
                    document.toObject<User>()
                }
                .filter { it?.userName == deal.author }

            if (authorList.firstOrNull() == null) {
                Log.d(TAG, "User wasn't found in balance adjust query")
            } else {

                var author = authorList[0]
                var currentBalance = author?.totalOwedTo

                currentBalance += deal.debtSum?.toDouble() ?: 0.0   // increase balance
                db.collection("users")
                    .document("${deal.debtor}")
                    .update("totalOwedFrom", currentBalance.toString())
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }


                /**
                 *  adjust author balance +
                 */
                db.collection("users")
                    .document("${deal.author}")
                    .get()
                    .addOnSuccessListener { documents ->
                        currentBalance = documents.get("totalOwedTo").toString()
                        Log.d(TAG, "current balance ${currentBalance} queried")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Error getting documents")
                    }

                currentBalance += deal.debtSum?.toDouble() ?: 0.0   // increase balance
                db.collection("users")
                    .document("${deal.debtor}")
                    .update("totalOwedTo", currentBalance.toString())
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }
        }
    }

}