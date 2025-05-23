package com.orbitalsonic.adsserverbase.ui.startup.fragments

import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.AdmobInterstitial
import com.orbitalsonic.adsserverbase.adsconfig.interstitial.callbacks.InterstitialOnShowCallBack
import com.orbitalsonic.adsserverbase.adsconfig.natives.AdmobNativePreload
import com.orbitalsonic.adsserverbase.adsconfig.natives.enums.LayoutType
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.FragmentStartupLanguageBinding
import com.orbitalsonic.adsserverbase.ui.base.fragments.BaseFragment
import com.orbitalsonic.adsserverbase.ui.startup.StartupActivity

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class FragmentStartupLanguage :
    BaseFragment<FragmentStartupLanguageBinding>(FragmentStartupLanguageBinding::inflate) {

    private val admobInterstitial by lazy { AdmobInterstitial() }
    private val admobNativePreload by lazy { AdmobNativePreload() }

    private val sharedPrefManager by lazy {
        SharedPrefManager(
            requireActivity().getSharedPreferences(
                "app_preferences",
                MODE_PRIVATE
            )
        )
    }

    override fun onViewCreated() {
        binding.mbContinueLanguage.setOnClickListener { onContinueClick() }

        showNativeAd()
    }

    /**
     * Add Service in Manifest first
     */

    private fun onContinueClick() {
        if (isAdded) {
            sharedPrefManager.isFirstTimeEntrance = false
            (activity as StartupActivity).nextActivity()
            admobInterstitial.showInterstitialAd(activity, object : InterstitialOnShowCallBack {
                override fun onAdDismissedFullScreenContent() {}
                override fun onAdFailedToShowFullScreenContent() {}
                override fun onAdShowedFullScreenContent() {}
                override fun onAdImpression() {}

            })
        }
    }

    private fun showNativeAd() {
        if (isAdded) {
            admobNativePreload.showNativeAds(
                activity,
                binding.adsPlaceHolder,
                LayoutType.LARGE_ADJUSTED
            )
        }
    }
}