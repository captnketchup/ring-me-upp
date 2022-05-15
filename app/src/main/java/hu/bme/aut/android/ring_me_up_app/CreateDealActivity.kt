package hu.bme.aut.android.ring_me_up_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.ring_me_up_app.data.Deal
import hu.bme.aut.android.ring_me_up_app.databinding.ActivityCreateDealBinding
import hu.bme.aut.android.ring_me_up_app.extensions.validateNonEmpty

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

        val userArray = getAllUsers()        // this returns an array of users

        val userArrayForSpinner = arrayOfNulls<String>(userArray.size)      // this is for the username of the users
        for(i in userArray.indices){
            userArrayForSpinner[i] = userArray[i].userName
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
//        binding.etDebtor.validateNonEmpty() && binding.etDebtSum.validateNonEmpty()
        binding.etDebtSum.validateNonEmpty()

    private fun uploadDeal() {
        val newDeal = Deal(
            uid,
            userName,
            debtorName,
            binding.etDebtSum.text.toString() + " Ft"
        )

        val db = Firebase.firestore

        db.collection("deals")
            .add(newDeal)
            .addOnSuccessListener {
                toast("Deal was created")
                finish()
            }
            .addOnFailureListener { e -> toast(e.toString()) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}