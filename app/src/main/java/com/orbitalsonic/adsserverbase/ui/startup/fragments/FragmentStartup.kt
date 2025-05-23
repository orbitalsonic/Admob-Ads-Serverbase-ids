package com.orbitalsonic.adsserverbase.ui.startup.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.AdmobInterstitial
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnLoadCallBack
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnShowCallBack
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsProviderType.interAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.firebase.RemoteConfiguration
import com.orbitalsonic.adsserverbase.common.network.InternetManager
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.FragmentStartupBinding
import com.orbitalsonic.adsserverbase.helpers.handlers.withDelay
import com.orbitalsonic.adsserverbase.helpers.lifecycle.launchWhenResumed
import com.orbitalsonic.adsserverbase.ui.base.fragments.BaseFragment
import com.orbitalsonic.adsserverbase.ui.startup.StartupActivity

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class FragmentStartup : BaseFragment<FragmentStartupBinding>(FragmentStartupBinding::inflate) {

    private val admobInterstitial by lazy { AdmobInterstitial() }
    private val adsRepository by lazy { AdsRepository() }

    private val mHandler = Handler(Looper.getMainLooper())
    private val adsRunner = Runnable { checkAdvertisement() }
    private var isInterLoadOrFailed = false
    private var mCounter: Int = 0

    private var startTime = 0L

    private val sharedPrefManager by lazy {
        SharedPrefManager(
            requireActivity().getSharedPreferences(
                "app_preferences",
                MODE_PRIVATE
            )
        )
    }

    private val internetManager by lazy {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        InternetManager(connectivityManager)
    }

    private val remoteConfiguration by lazy {
        RemoteConfiguration(internetManager)
    }

    override fun onViewCreated() {
        fetchRemoteConfiguration()
    }

    private fun fetchRemoteConfiguration() {
        remoteConfiguration.checkRemoteConfig { fetchSuccessfully ->
            if (isAdded) {
                if (fetchSuccessfully) {
                    mCounter = 0
                    loadAds()
                } else {
                    mHandler.removeCallbacks { adsRunner }
                    moveNext(2000)
                }
            }
        }
    }

    private fun loadAds() {
        if (isAdded) {
            startTime = System.currentTimeMillis()
            Log.d("AdsInformation", "Call Admob Splash Interstitial")
            admobInterstitial.loadInterstitialAd(
                activity = activity,
                adsControl = adsRepository.getAdConfig(
                    interAd, sharedPrefManager.isAppPurchased,
                    internetManager.isInternetConnected
                ),
                object : InterstitialOnLoadCallBack {
                    override fun onAdFailedToLoad(adError: String) {
                        isInterLoadOrFailed = true
                    }

                    override fun onAdLoaded() {
                        isInterLoadOrFailed = true
                        val endTime = System.currentTimeMillis()
                        val loadingTime: Int = ((endTime - startTime) / 1000).toInt()
                        Log.d("AdsInformation", "InterLoadingTime: ${loadingTime}s")
                    }

                    override fun onPreloaded() {
                        isInterLoadOrFailed = true
                    }

                })
        }
    }

    private fun checkAdvertisement() {
        if (internetManager.isInternetConnected) {
            if (mCounter < 12) {
                try {
                    mCounter++
                    if (isInterLoadOrFailed) {
                        moveNext()
                        mHandler.removeCallbacks { adsRunner }
                    } else {
                        mHandler.removeCallbacks { adsRunner }
                        mHandler.postDelayed(
                            adsRunner,
                            (1000)
                        )
                    }

                } catch (e: Exception) {
                    Log.e("checkAdvertisementTAG", "${e.message}")
                }
            } else {
                moveNext()
                mHandler.removeCallbacks { adsRunner }
            }
        } else {
            moveNext(3000)
        }

    }

    private fun moveNext(timeMilli: Long = 500) {
        withDelay(timeMilli) {
            launchWhenResumed {
                if (isAdded) {
                    (activity as StartupActivity).nextActivity()
                    admobInterstitial.showInterstitialAd(activity, object :
                        InterstitialOnShowCallBack {
                        override fun onAdDismissedFullScreenContent() {}
                        override fun onAdFailedToShowFullScreenContent() {}
                        override fun onAdShowedFullScreenContent() {}
                        override fun onAdImpression() {}

                    })
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopHandler()
    }

    override fun onResume() {
        super.onResume()
        resumeHandler()
    }

    private fun stopHandler() {
        mHandler.removeCallbacks(adsRunner)
    }

    private fun resumeHandler() {
        mHandler.post(adsRunner)
    }

}