package com.orbitalsonic.adsserverbase.ui.startup

import android.content.Intent
import androidx.navigation.fragment.NavHostFragment
import com.orbitalsonic.adsserverbase.R
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.ActivityStartupBinding
import com.orbitalsonic.adsserverbase.helpers.ui.goBackPressed
import com.orbitalsonic.adsserverbase.ui.MainActivity
import com.orbitalsonic.adsserverbase.ui.base.activities.BaseActivity

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class StartupActivity : BaseActivity<ActivityStartupBinding>(ActivityStartupBinding::inflate) {

    private val sharedPrefManager by lazy {
        SharedPrefManager(
            getSharedPreferences(
                "app_preferences",
                MODE_PRIVATE
            )
        )
    }

    override fun onCreated() {
        checkCaseType()
        goBackPressed {}
    }

    private fun checkCaseType() {
        navigateScreen()
    }

    fun nextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateScreen() {
        val navController =
            (supportFragmentManager.findFragmentById(binding.fcvContainerSplash.id) as NavHostFragment).navController
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph_startup)
        if (sharedPrefManager.isFirstTimeEntrance) {
            navGraph.setStartDestination(R.id.fragmentStartupStart)
        } else {
            navGraph.setStartDestination(R.id.fragmentStartup)
        }
        navController.graph = navGraph
    }
}