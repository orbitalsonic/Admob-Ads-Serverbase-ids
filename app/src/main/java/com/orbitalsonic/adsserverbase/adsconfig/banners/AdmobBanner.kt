package com.orbitalsonic.adsserverbase.adsconfig.banners

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.orbitalsonic.adsserverbase.adsconfig.banners.callbacks.BannerCallBack
import com.orbitalsonic.adsserverbase.adsconfig.banners.enums.BannerType
import com.orbitalsonic.adsserverbase.adsconfig.utils.models.Ads

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class AdmobBanner {
    private var adView: AdView? = null

    fun loadBannerAds(
        activity: Activity?,
        adsPlaceHolder: FrameLayout,
        adsControl: Ads?,
        bannerType: BannerType = BannerType.ADAPTIVE_BANNER,
        bannerCallBack: BannerCallBack? = null
    ) {
        if (adsControl == null) {
            Log.e("AdsInformation", "loadBannerAds -> adsControl is null")
            bannerCallBack?.onAdFailedToLoad("loadBannerAds -> adsControl is null")
            return
        }

        if (adsControl.isAppPurchased) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Premium user")
            bannerCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Premium user")
            return
        }

        if (adsControl.isEnabled.not()) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Remote config is off")
            bannerCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Remote config is off")
            return
        }

        if (adsControl.isInternetConnected.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Internet is not connected"
            )
            bannerCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Internet is not connected")
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Context is null")
            bannerCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Context is null")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> activity is finishing or destroyed"
            )
            bannerCallBack?.onAdFailedToLoad("onAdFailedToLoad -> activity is finishing or destroyed")
            return
        }

        if (adsControl.adID.trim().isEmpty()) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Ad id is empty")
            bannerCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Ad id is empty")
            return
        }

        try {
            adsPlaceHolder.visibility = View.VISIBLE
            adView = AdView(activity)
            adView?.adUnitId = adsControl.adID
            try {
                adView?.setAdSize(getAdSize(activity, adsPlaceHolder))
            } catch (ex: Exception) {
                adView?.setAdSize(AdSize.BANNER)
            }

            val adRequest: AdRequest = when (bannerType) {
                BannerType.ADAPTIVE_BANNER -> {
                    AdRequest
                        .Builder()
                        .build()
                }

                BannerType.COLLAPSIBLE_BOTTOM -> {
                    AdRequest
                        .Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
                            putString("collapsible", "bottom")
                        })
                        .build()
                }

                BannerType.COLLAPSIBLE_TOP -> {
                    AdRequest
                        .Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
                            putString("collapsible", "top")
                        })
                        .build()
                }
            }

            adView?.loadAd(adRequest)
            adView?.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.i("AdsInformation", "admob ${adsControl.type} onAdLoaded")
                    displayBannerAd(adsPlaceHolder)
                    bannerCallBack?.onAdLoaded()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(
                        "AdsInformation",
                        "admob ${adsControl.type} onAdFailedToLoad: ${adError.message}"
                    )
                    adsPlaceHolder.visibility = View.GONE
                    bannerCallBack?.onAdFailedToLoad(adError.message)
                }

                override fun onAdImpression() {
                    Log.d("AdsInformation", "admob ${adsControl.type} onAdImpression")
                    bannerCallBack?.onAdImpression()
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    Log.d("AdsInformation", "admob ${adsControl.type} onAdClicked")
                    bannerCallBack?.onAdClicked()
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    Log.d("AdsInformation", "admob ${adsControl.type} onAdClosed")
                    bannerCallBack?.onAdClosed()
                    super.onAdClosed()
                }

                override fun onAdOpened() {
                    Log.d("AdsInformation", "admob ${adsControl.type} onAdOpened")
                    bannerCallBack?.onAdOpened()
                    super.onAdOpened()
                }
            }
        } catch (ex: Exception) {
            Log.e("AdsInformation", "${ex.message}")
            bannerCallBack?.onAdFailedToLoad("${ex.message}")
        }

    }

    private fun displayBannerAd(adsPlaceHolder: FrameLayout) {
        try {
            if (adView != null) {
                val viewGroup: ViewGroup? = adView?.parent as? ViewGroup?
                viewGroup?.removeView(adView)

                adsPlaceHolder.removeAllViews()
                adsPlaceHolder.addView(adView)
            } else {
                adsPlaceHolder.removeAllViews()
                adsPlaceHolder.visibility = View.GONE
            }
        } catch (ex: Exception) {
            Log.e("AdsInformation", "inflateBannerAd: ${ex.message}")
        }

    }

    fun bannerOnPause() {
        try {
            adView?.pause()
        } catch (ex: Exception) {
            Log.e("AdsInformation", "bannerOnPause: ${ex.message}")
        }

    }

    fun bannerOnResume() {
        try {
            adView?.resume()
        } catch (ex: Exception) {
            Log.e("AdsInformation", "bannerOnPause: ${ex.message}")
        }
    }

    fun bannerOnDestroy() {
        try {
            adView?.destroy()
            adView = null
        } catch (ex: Exception) {
            Log.e("AdsInformation", "bannerOnPause: ${ex.message}")
        }
    }

    @Suppress("DEPRECATION")
    @Throws(Exception::class)
    private fun getAdSize(mActivity: Activity, adContainer: FrameLayout): AdSize {
        val display = mActivity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth)
    }

}