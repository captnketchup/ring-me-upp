package hu.bme.aut.android.ring_me_up_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.ring_me_up_app.adapter.DealsAdapter
import hu.bme.aut.android.ring_me_up_app.data.Deal
import hu.bme.aut.android.ring_me_up_app.databinding.ActivityDealsBinding

class DealsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDealsBinding
    private lateinit var dealsAdapter: DealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDealsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.appBarDeals.toolbar)

        binding.appBarDeals.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        binding.navView.setNavigationItemSelectedListener(this)

        // connecting recyclerview
        dealsAdapter = DealsAdapter(applicationContext)
        binding.appBarDeals.contentDeals.rvDeals.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        binding.appBarDeals.contentDeals.rvDeals.adapter = dealsAdapter

        binding.appBarDeals.fab.setOnClickListener {
            val createDealIntent = Intent(this, CreateDealActivity::class.java)
            startActivity(createDealIntent)
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        }

        initDealsListener()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                finish()
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, UserActivity::class.java))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initDealsListener() {
        val db = Firebase.firestore
        db.collection("deals")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> dealsAdapter.addDeal(dc.document.toObject<Deal>())
                        DocumentChange.Type.REMOVED -> {
                            finish()
                            startActivity(intent)
                        }
                    }
                }
            }
    }

    override fun onBackPressed() {
        Toast.makeText(
            this,
            "Press logout on the sliding nav component to logout",
            Toast.LENGTH_SHORT
        ).show()
    }
}