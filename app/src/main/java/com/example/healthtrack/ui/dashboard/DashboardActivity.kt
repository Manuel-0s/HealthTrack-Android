package com.example.healthtrack.ui.dashboard

import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.healthtrack.R
import com.example.healthtrack.databinding.ActivityDashboardBinding
import com.example.healthtrack.ui.appointment.AppointmentFragment
import com.example.healthtrack.ui.auth.AuthActivity
import com.example.healthtrack.ui.home.HomeFragment
import com.example.healthtrack.ui.metrics.MetricsFragment
import com.example.healthtrack.ui.prescription.PrescriptionFragment
import com.example.healthtrack.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarAndDrawer()
        setupHeaderUserData()
        setupLogoutObserver()

        binding.bottomNavigationView.setupBottomMenuLogic()
        binding.navigationView.setupDrawerMenuLogic()

        binding.fabAdd.setOnClickListener {
            showAddMetricBottomSheet()
        }

        if (savedInstanceState == null) {
            changeFragment(HomeFragment())
            binding.bottomNavigationView.selectedItemId = R.id.nav_home
        }
        
        viewModel.loadUserData()
    }

    private fun setupHeaderUserData() {
        val headerView = binding.navigationView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.tvHeaderEmail)

        viewModel.userData.observe(this) { user ->
            user?.let {
                tvHeaderName.text = it.nombre
                tvHeaderEmail.text = it.correo
            }
        }
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun BottomNavigationView.setupBottomMenuLogic() {
        setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    changeFragment(HomeFragment())
                    true
                }
                R.id.nav_metrics -> {
                    changeFragment(MetricsFragment())
                    true
                }
                R.id.nav_appointment -> {
                    changeFragment(AppointmentFragment())
                    true
                }
                R.id.nav_prescription -> {
                    changeFragment(PrescriptionFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupLogoutObserver() {
        viewModel.loggedOut.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(this, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun NavigationView.setupDrawerMenuLogic() {
        setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_accont -> {
                    changeFragment(ProfileFragment())
                }
                R.id.nav_logout -> {
                    viewModel.logout()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
        }
    }

    private fun showAddMetricBottomSheet() {
        if (supportFragmentManager.findFragmentByTag(AddMetricBottomSheet.TAG) == null) {
            AddMetricBottomSheet.newInstance().show(supportFragmentManager, AddMetricBottomSheet.TAG)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}