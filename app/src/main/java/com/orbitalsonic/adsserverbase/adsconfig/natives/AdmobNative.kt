package com.orbitalsonic.adsserverbase.adsconfig.natives

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.orbitalsonic.adsserverbase.R
import com.orbitalsonic.adsserverbase.adsconfig.natives.callbacks.NativeCallBack
import com.orbitalsonic.adsserverbase.adsconfig.natives.enums.LayoutType
import com.orbitalsonic.adsserverbase.adsconfig.utils.ScreenUtils.isSupportFullScreen
import com.orbitalsonic.adsserverbase.adsconfig.utils.models.Ads
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class AdmobNative {

    private var adMobNativeAd: NativeAd? = null

    /**
     * load native ad and show
     */
    fun loadNativeAds(
        activity: Activity?,
        adsPlaceHolder: FrameLayout,
        adsControl: Ads?,
        layoutType: LayoutType,
        nativeCallBack: NativeCallBack? = null
    ) {
        val handlerException = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("AdsInformation", "${throwable.message}")
            nativeCallBack?.onAdFailedToLoad("${throwable.message}")
        }

        if (adsControl == null) {
            Log.e("AdsInformation", "loadNativeAds -> adsControl is null")
            nativeCallBack?.onAdFailedToLoad("loadNativeAds -> adsControl is null")
            return
        }

        if (adsControl.isAppPurchased) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Premium user")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Premium user")
            return
        }

        if (adsControl.isEnabled.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Remote config is off"
            )
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Remote config is off")
            return
        }

        if (adsControl.isInternetConnected.not()) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> Internet is not connected"
            )
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Internet is not connected")
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Context is null")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Context is null")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e(
                "AdsInformation",
                "${adsControl.type} onAdFailedToLoad -> activity is finishing or destroyed"
            )
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> activity is finishing or destroyed")
            return
        }

        if (adsControl.adID.trim().isEmpty()) {
            Log.e("AdsInformation", "${adsControl.type} onAdFailedToLoad -> Ad id is empty")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Ad id is empty")
            return
        }

        try {
            adsPlaceHolder.visibility = View.VISIBLE

            if (adMobNativeAd == null) {
                CoroutineScope(Dispatchers.IO + handlerException).launch {
                    val builder: AdLoader.Builder =
                        AdLoader.Builder(activity, adsControl.adID)
                    val adLoader =
                        builder.forNativeAd { unifiedNativeAd: NativeAd? ->
                            adMobNativeAd = unifiedNativeAd
                        }
                            .withAdListener(object : AdListener() {
                                override fun onAdImpression() {
                                    super.onAdImpression()
                                    Log.d(
                                        "AdsInformation",
                                        "admob ${adsControl.type} onAdImpression"
                                    )
                                    nativeCallBack?.onAdImpression()
                                    adMobNativeAd = null
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    Log.e(
                                        "AdsInformation",
                                        "admob ${adsControl.type} onAdFailedToLoad: ${loadAdError.message}"
                                    )
                                    nativeCallBack?.onAdFailedToLoad(loadAdError.message)
                                    adsPlaceHolder.visibility = View.GONE
                                    adMobNativeAd = null
                                    super.onAdFailedToLoad(loadAdError)
                                }

                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    Log.i(
                                        "AdsInformation",
                                        "admob ${adsControl.type} onAdLoaded"
                                    )
                                    nativeCallBack?.onAdLoaded()
                                    displayNativeAd(activity, adsPlaceHolder, layoutType)

                                }

                            }).withNativeAdOptions(
                                com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                                    .setAdChoicesPlacement(
                                        NativeAdOptions.ADCHOICES_TOP_RIGHT
                                    ).build()
                            )
                            .build()
                    adLoader.loadAd(AdRequest.Builder().build())

                }
            } else {
                Log.i("AdsInformation", "${adsControl.type} is already onPreloaded")
                nativeCallBack?.onPreloaded()
                displayNativeAd(activity, adsPlaceHolder, layoutType)
            }
        } catch (ex: Exception) {
            Log.e("AdsInformation", "${ex.message}")
            nativeCallBack?.onAdFailedToLoad("${ex.message}")
        }
    }

    private fun displayNativeAd(
        activity: Activity?,
        adMobNativeContainer: FrameLayout,
        layoutType: LayoutType,
    ) {
        activity?.let { mActivity ->
            try {
                adMobNativeAd?.let { ad ->
                    val inflater = LayoutInflater.from(mActivity)

                    val adView: NativeAdView = when (layoutType) {
                        LayoutType.BANNER -> inflater.inflate(
                            R.layout.native_banner,
                            null
                        ) as NativeAdView

                        LayoutType.SMALL -> inflater.inflate(
                            R.layout.native_small,
                            null
                        ) as NativeAdView

                        LayoutType.LARGE -> inflater.inflate(
                            R.layout.native_large,
                            null
                        ) as NativeAdView

                        LayoutType.LARGE_ADJUSTED -> if (mActivity.isSupportFullScreen()) {
                            inflater.inflate(R.layout.native_large, null) as NativeAdView
                        } else {
                            inflater.inflate(R.layout.native_small, null) as NativeAdView
                        }
                    }
                    val viewGroup: ViewGroup? = adView.parent as ViewGroup?
                    viewGroup?.removeView(adView)

                    adMobNativeContainer.removeAllViews()
                    adMobNativeContainer.addView(adView)

                    if (layoutType == LayoutType.LARGE) {
                        val mediaView: MediaView = adView.findViewById(R.id.media_view)
                        adView.mediaView = mediaView
                    }
                    if (layoutType == LayoutType.LARGE_ADJUSTED) {
                        if (mActivity.isSupportFullScreen()) {
                            val mediaView: MediaView = adView.findViewById(R.id.media_view)
                            adView.mediaView = mediaView
                        }
                    }

                    // Set other ad assets.
                    adView.headlineView = adView.findViewById(R.id.ad_headline)
                    adView.bodyView = adView.findViewById(R.id.ad_body)
                    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                    adView.iconView = adView.findViewById(R.id.ad_app_icon)

                    //Headline
                    adView.headlineView?.let { headline ->
                        (headline as TextView).text = ad.headline
                        headline.isSelected = true
                    }

                    //Body
                    adView.bodyView?.let { bodyView ->
                        if (ad.body == null) {
                            bodyView.visibility = View.INVISIBLE
                        } else {
                            bodyView.visibility = View.VISIBLE
                            (bodyView as TextView).text = ad.body
                        }
                    }

                    //Call to Action
                    adView.callToActionView?.let { ctaView ->
                        if (ad.callToAction == null) {
                            ctaView.visibility = View.GONE
                        } else {
                            ctaView.visibility = View.VISIBLE
                            (ctaView as Button).text = ad.callToAction
                        }
                    }

                    //Icon
                    adView.iconView?.let { iconView ->
                        if (ad.icon == null) {
                            iconView.visibility = View.GONE
                        } else {
                            (iconView as ImageView).setImageDrawable(ad.icon?.drawable)
                            iconView.visibility = View.VISIBLE
                        }
                    }

                    adView.advertiserView?.let { adverView ->
                        if (ad.advertiser == null) {
                            adverView.visibility = View.GONE
                        } else {
                            (adverView as TextView).text = ad.advertiser
                            adverView.visibility = View.GONE
                        }
                    }

                    adView.setNativeAd(ad)
                }
            } catch (ex: Exception) {
                Log.e("AdsInformation", "displayNativeAd: ${ex.message}")
            }
        }
    }
}