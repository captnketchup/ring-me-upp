package hu.bme.aut.android.ring_me_up_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.ring_me_up_app.data.Deal
import hu.bme.aut.android.ring_me_up_app.databinding.ActivityCreateDealBinding
import hu.bme.aut.android.ring_me_up_app.extensions.validateNonEmpty

class CreateDealActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE = 101
    }

    private lateinit var binding: ActivityCreateDealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDealBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.btnSend.setOnClickListener { sendClick() }
    }

    private fun sendClick() {
        if (!validateForm()) {
            return
        }

        uploadDeal()
    }

    private fun validateForm() =
        binding.etDebtor.validateNonEmpty() && binding.etDebtSum.validateNonEmpty()

    private fun uploadDeal() {
        val newDeal = Deal(
            uid,
            userName,
            binding.etDebtor.text.toString(),
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