package com.orbitalsonic.adsserverbase.adsconfig.appOpen

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.isOpenAdLoading
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.mAppOpenAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsProviderType.appOpenAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.ui.startup.StartupActivity
import java.util.Date

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class AdmobAppOpen(private val myApplication: Application) : LifecycleObserver,
    ActivityLifecycleCallbacks {
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0

    private val sharedPrefManager by lazy {
        SharedPrefManager(
            myApplication.getSharedPreferences(
                "app_preferences",
                MODE_PRIVATE
            )
        )
    }
    private val adsControl = AdsRepository().getAdConfig(appOpenAd,sharedPrefManager.isAppPurchased,true)

    /**
     * 0 = Ads Off
     * 1 = Ads On
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (adsControl?.isAppPurchased?.not() == true && adsControl.isEnabled) {
            showAdIfAvailable()
        }
    }

    fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable) {
            return
        }
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                isOpenAdLoading = false
                mAppOpenAd = appOpenAd
                Log.i("AdsInformation", "appOpen onAdLoaded")

                mAppOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("AdsInformation", "appOpen onAdDismissedFullScreenContent")
                        mAppOpenAd = null
                        isShowingAd = false
                        fetchAd()
                        if (appOpenListener != null) {
                            appOpenListener?.onOpenAdClosed()
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        if (appOpenListener != null) {
                            appOpenListener?.onOpenAdClosed()
                            Log.e("AdsInformation", "appOpen onAdFailedToShowFullScreenContent")
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("AdsInformation", "appOpen onAdShowedFullScreenContent")
                        isShowingAd = true
                    }
                }
                loadTime = Date().time
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e("AdsInformation", "appOpen onAdFailedToLoad")
                isOpenAdLoading = false
                mAppOpenAd = null
            }
        }

        if (adsControl?.isAppPurchased?.not() == true && adsControl.isEnabled) {
            if (mAppOpenAd == null && !isOpenAdLoading) {
                isOpenAdLoading = true
                try {
                    AppOpenAd.load(
                        myApplication,
                        adsControl.adID,
                        AdRequest.Builder().build(),
                        loadCallback
                    )
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private fun showAdIfAvailable() {
        if (adsControl?.isAppPurchased?.not() == true && adsControl.isEnabled) {
            if (currentActivity is StartupActivity || currentActivity is AdActivity)
                return

            mAppOpenAd?.show(currentActivity!!)
        } else {
            fetchAd()
        }
    }

    private var appOpenListener: AppOpenListener? = null

    interface AppOpenListener {
        fun onOpenAdClosed()
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4.toLong()
    }

    private val isAdAvailable: Boolean
        get() = mAppOpenAd != null && wasLoadTimeLessThanNHoursAgo()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    companion object {
        private var isShowingAd = false
    }

    init {
        this.myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

}