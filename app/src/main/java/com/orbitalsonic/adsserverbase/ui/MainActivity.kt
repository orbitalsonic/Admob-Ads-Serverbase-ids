package com.orbitalsonic.adsserverbase.ui

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.orbitalsonic.adsserverbase.R
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.AdmobInterstitial
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnLoadCallBack
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnShowCallBack
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.rcvRemoteCounter
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.totalCount
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsProviderType.interAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.network.InternetManager
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.ActivityMainBinding
import com.orbitalsonic.adsserverbase.helpers.ui.statusBarColorUpdate
import com.orbitalsonic.adsserverbase.ui.base.activities.BaseActivity

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val admobInterstitial by lazy { AdmobInterstitial() }

    private val adsRepository by lazy { AdsRepository() }

    private val sharedPrefManager by lazy { SharedPrefManager(getSharedPreferences(
        "app_preferences",
        MODE_PRIVATE
    )) }

    private val internetManager by lazy {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        InternetManager(connectivityManager)
    }

    override fun onCreated() {
        statusBarColorUpdate(R.color.primary600)
        initToolbar()
        initNavController()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }


    private fun initNavController() {
        navController =
            (supportFragmentManager.findFragmentById(binding.navHostFragmentContainer.id) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentHome))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun checkCounter(){
        try {
            if (admobInterstitial.isInterstitialLoaded()){
                showInterstitialAd()
                totalCount += 1
            }else{
                if (totalCount >= rcvRemoteCounter) {
                    totalCount = 1
                    loadInterstitialAd()
                }else{
                    totalCount += 1
                }
            }
        }catch (e:Exception){
            Log.d("AdsInformation","${e.message}")
        }
    }


    fun loadInterstitialAd(){
        admobInterstitial.loadInterstitialAd(
            activity = this,
            adsControl = adsRepository.getAdConfig(
                interAd,
                sharedPrefManager.isAppPurchased,
                internetManager.isInternetConnected
            ),
            object : InterstitialOnLoadCallBack {
                override fun onAdFailedToLoad(adError: String) {}
                override fun onAdLoaded() {}
                override fun onPreloaded() {}
            }
        )
    }

    fun showInterstitialAd(){
        admobInterstitial.showInterstitialAd(
            this,
            object : InterstitialOnShowCallBack {
                override fun onAdDismissedFullScreenContent() {}
                override fun onAdFailedToShowFullScreenContent() {}
                override fun onAdShowedFullScreenContent() {}
                override fun onAdImpression() {}
            }
        )
    }
}