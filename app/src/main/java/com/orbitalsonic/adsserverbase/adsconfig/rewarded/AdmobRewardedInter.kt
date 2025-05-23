package com.orbitalsonic.adsserverbase.adsconfig.rewarded

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.orbitalsonic.adsserverbase.adsconfig.rewarded.callbacks.RewardedOnLoadCallBack
import com.orbitalsonic.adsserverbase.adsconfig.rewarded.callbacks.RewardedOnShowCallBack
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.isInterLoading
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.isRewardedInterLoading
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.rewardedInterAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.models.Ads

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class AdmobRewardedInter {

    /**
     * 0 = Ads Off
     * 1 = Ads On
     */

    fun loadRewardedAd(
        activity: Activity?,
        adsControl: Ads?,
        callback: RewardedOnLoadCallBack? = null
    ) {
        if (adsControl == null) {
            Log.e("AdsInformation", "loadRewardedAd -> adsControl is null")
            callback?.onAdFailedToLoad("loadRewardedAd -> adsControl is null")
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

        if (isRewardedInterLoading) {
            Log.e("AdsInformation", "onAdFailedToLoad -> rewarded inter is loading...")
            callback?.onAdFailedToLoad("onAdFailedToLoad -> rewarded inter is loading...")
            return
        }
        
        try {
            if (rewardedInterAd == null) {
                isRewardedInterLoading = true
                RewardedInterstitialAd.load(
                    activity,
                    adsControl.adID,
                    AdRequest.Builder().build(),
                    object : RewardedInterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e(
                                "AdsInformation",
                                "admob ${adsControl.type} onAdFailedToLoad: ${adError.message}"
                            )
                            isRewardedInterLoading = false
                            rewardedInterAd = null
                            callback?.onAdFailedToLoad(adError.toString())
                        }

                        override fun onAdLoaded(ad: RewardedInterstitialAd) {
                            Log.i("AdsInformation", "admob ${adsControl.type} onAdLoaded")
                            isRewardedInterLoading = false
                            rewardedInterAd = ad
                            callback?.onAdLoaded()
                        }
                    })
            } else {
                Log.i("AdsInformation", "admob ${adsControl.type} onPreloaded")
                callback?.onPreloaded()
            } 
        }catch (ex:Exception){
            Log.e("AdsInformation", "${ex.message}")
        }
    }

    fun showRewardedAd(activity: Activity?, listener: RewardedOnShowCallBack) {
        activity?.let { mActivity ->
            if (rewardedInterAd != null) {
                rewardedInterAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                    override fun onAdDismissedFullScreenContent() {
                        Log.d("AdsInformation", "admob Rewarded onAdDismissedFullScreenContent")
                        listener.onAdDismissedFullScreenContent()
                        rewardedInterAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e("AdsInformation", "admob Rewarded Interstitial onAdFailedToShowFullScreenContent: ${adError.message}")
                        listener.onAdFailedToShowFullScreenContent()
                        rewardedInterAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("AdsInformation", "admob Rewarded Interstitial onAdShowedFullScreenContent")
                        listener.onAdShowedFullScreenContent()
                        rewardedInterAd = null
                    }

                    override fun onAdImpression() {
                        Log.d("AdsInformation", "admob Rewarded Interstitial onAdImpression")
                        listener.onAdImpression()
                    }
                }
                rewardedInterAd?.let { ad ->
                    ad.show(mActivity) { rewardItem ->
                        Log.i("AdsInformation", "admob Rewarded Interstitial onUserEarnedReward")
                        listener.onUserEarnedReward()
                    }
                }
            }
        }
    }

    fun isRewardedLoaded(): Boolean {
        return rewardedInterAd != null
    }

    fun dismissRewarded() {
        rewardedInterAd = null
    }
}