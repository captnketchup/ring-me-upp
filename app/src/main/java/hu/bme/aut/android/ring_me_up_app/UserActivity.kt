package hu.bme.aut.android.ring_me_up_app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import hu.bme.aut.android.ring_me_up_app.databinding.ActivityUserBinding

class UserActivity : BaseActivity() {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        binding = ActivityUserBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        var balance: Double = 0.0
        var youOwe: Double = 0.0
        var yourOwed: Double = 0.0
        binding.tvHelloUser.text = "Hello ${userName}!"
        binding.tvBalance.text = "Your current balance is: ${balance}"
        binding.tvYouOwe.text = "Overall you owe: ${youOwe}"
        binding.tvYouOwe.setTextColor(Color.parseColor("#ab4f4f"))
        binding.tvYoureOwed.text = "Overall you're owed ${yourOwed}"
        binding.tvYoureOwed.setTextColor(Color.parseColor("#a8cf95"))

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
    }
}