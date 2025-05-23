package com.orbitalsonic.adsserverbase.adsconfig.interstitial

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnLoadCallBack
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnShowCallBack
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.isInterLoading
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.mInterstitialAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.models.Ads

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class AdmobInterstitial {

    fun loadInterstitialAd(
        activity: Activity?,
        adsControl: Ads?,
        callback: InterstitialOnLoadCallBack? = null
    ) {
        if (adsControl == null) {
            Log.e("AdsInformation", "loadInterAds -> adsControl is null")
            callback?.onAdFailedToLoad("loadInterAds -> adsControl is null")
            return
        }

        if (adsControl.isAppPurchased) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Premium user")
            callback?.onAdFailedToLoad("onAdFailedToLoad -> Premium user")
            return
        }

        if (adsControl.isEnabled.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Remote config is off"
            )
            callback?.onAdFailedToLoad("onAdFailedToLoad -> Remote config is off")
            return
        }

        if (adsControl.isInternetConnected.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Internet is not connected"
            )
            callback?.onAdFailedToLoad("onAdFailedToLoad -> Internet is not connected")
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Context is null")
            callback?.onAdFailedToLoad("onAdFailedToLoad -> Context is null")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> activity is finishing or destroyed"
            )
            callback?.onAdFailedToLoad("onAdFailedToLoad -> activity is finishing or destroyed")
            return
        }

        if (adsControl.adID.trim().isEmpty()) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Ad id is empty")
            callback?.onAdFailedToLoad("onAdFailedToLoad -> Ad id is empty")
            return
        }

        if (isInterLoading) {
            Log.e("AdsInformation", "onAdFailedToLoad -> interstitial is loading...")
            callback?.onAdFailedToLoad("onAdFailedToLoad -> interstitial is loading...")
            return
        }

        try {
            if (mInterstitialAd == null) {
                isInterLoading = true
                InterstitialAd.load(
                    activity,
                    adsControl.adID,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e(
                                "AdsInformation",
                                "admob ${adsControl.type} onAdFailedToLoad: ${adError.message}"
                            )
                            isInterLoading = false
                            mInterstitialAd = null
                            callback?.onAdFailedToLoad(adError.toString())
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Log.i("AdsInformation", "admob ${adsControl.type} onAdLoaded")
                            isInterLoading = false
                            mInterstitialAd = interstitialAd
                            callback?.onAdLoaded()
                        }
                    })
            } else {
                Log.i("AdsInformation", "admob ${adsControl.type} onPreloaded")
                callback?.onPreloaded()
            }
        } catch (ex: Exception) {
            Log.e("AdsInformation", "${ex.message}")
        }
    }

    fun showInterstitialAd(activity: Activity?, listener: InterstitialOnShowCallBack? = null) {
        activity?.let { mActivity ->
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("AdsInformation", "admob Interstitial onAdDismissedFullScreenContent")
                        listener?.onAdDismissedFullScreenContent()
                        mInterstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e(
                            "AdsInformation",
                            "admob Interstitial onAdFailedToShowFullScreenContent: ${adError.message}"
                        )
                        listener?.onAdFailedToShowFullScreenContent()
                        mInterstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("AdsInformation", "admob Interstitial onAdShowedFullScreenContent")
                        listener?.onAdShowedFullScreenContent()
                        mInterstitialAd = null
                    }

                    override fun onAdImpression() {
                        Log.d("AdsInformation", "admob Interstitial onAdImpression")
                        listener?.onAdImpression()
                    }
                }
                mInterstitialAd?.show(mActivity)
            }
        }
    }

    fun showAndLoadInterstitialAd(
        activity: Activity?,
        adsControl: Ads?,
        listener: InterstitialOnShowCallBack? = null
    ) {
        activity?.let { mActivity ->
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(
                            "AdsInformation",
                            "admob ${adsControl?.type} onAdDismissedFullScreenContent"
                        )
                        listener?.onAdDismissedFullScreenContent()
                        loadAgainInterstitialAd(mActivity, adsControl)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e(
                            "AdsInformation",
                            "admob ${adsControl?.type} onAdFailedToShowFullScreenContent: ${adError.message}"
                        )

                        listener?.onAdFailedToShowFullScreenContent()
                        mInterstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(
                            "AdsInformation",
                            "admob ${adsControl?.type} onAdShowedFullScreenContent"
                        )
                        listener?.onAdShowedFullScreenContent()
                        mInterstitialAd = null
                    }

                    override fun onAdImpression() {
                        Log.d("AdsInformation", "admob ${adsControl?.type} onAdImpression")
                        listener?.onAdImpression()
                    }
                }
                mInterstitialAd?.show(mActivity)
            }
        }
    }

    private fun loadAgainInterstitialAd(
        activity: Activity?,
        adsControl: Ads?
    ) {
        if (adsControl == null) {
            Log.e("AdsInformation", "loadInterAds -> adsControl is null")
            return
        }

        if (adsControl.isAppPurchased) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Premium user")
            return
        }

        if (adsControl.isEnabled.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Remote config is off"
            )
            return
        }

        if (adsControl.isInternetConnected.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Internet is not connected"
            )
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Context is null")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> activity is finishing or destroyed"
            )
            return
        }

        if (adsControl.adID.trim().isEmpty()) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Ad id is empty")
            return
        }

        if (isInterLoading) {
            Log.e("AdsInformation", "onAdFailedToLoad -> interstitial is loading...")
            return
        }

        if (mInterstitialAd == null && !isInterLoading) {
            isInterLoading = true
            InterstitialAd.load(
                activity,
                adsControl.adID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e(
                            "AdsInformation",
                            "admob ${adsControl.type} onAdFailedToLoad: ${adError.message}"
                        )
                        isInterLoading = false
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.i("AdsInformation", "admob ${adsControl.type} onAdLoaded")
                        isInterLoading = false
                        mInterstitialAd = interstitialAd

                    }
                })
        }
    }

    fun isInterstitialLoaded(): Boolean {
        return mInterstitialAd != null
    }

    fun dismissInterstitial() {
        mInterstitialAd = null
    }

}