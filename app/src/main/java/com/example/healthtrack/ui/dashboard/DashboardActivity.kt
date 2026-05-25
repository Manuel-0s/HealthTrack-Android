package com.example.healthtrack.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.healthtrack.R
import com.example.healthtrack.databinding.ActivityDashboardBinding
import com.example.healthtrack.ui.home.HomeFragment
import com.example.healthtrack.ui.profile.ProfileFragment

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        binding.fabAdd.setOnClickListener {
            showAddMetricBottomSheet()
        }

        if (savedInstanceState == null) {
            changeFragment(HomeFragment())
            binding.bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    changeFragment(HomeFragment())
                    true
                }
                R.id.nav_metrics -> {
                    // Historial
                    true
                }
                R.id.nav_history_placeholder -> {
                    // Citas
                    true
                }
                R.id.nav_perfil -> {
                    changeFragment(ProfileFragment())
                    true
                }
                else -> false
            }
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
}
