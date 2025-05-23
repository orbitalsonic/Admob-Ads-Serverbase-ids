package com.orbitalsonic.adsserverbase.ui.samples

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.orbitalsonic.adsserverbase.adsconfig.natives.AdmobNative
import com.orbitalsonic.adsserverbase.adsconfig.natives.callbacks.NativeCallBack
import com.orbitalsonic.adsserverbase.adsconfig.natives.enums.LayoutType
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsProviderType.nativeAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.network.InternetManager
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.FragmentNativeLargeSampleBinding
import com.orbitalsonic.adsserverbase.ui.base.fragments.BaseFragment

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class FragmentNativeLargeSample : BaseFragment<FragmentNativeLargeSampleBinding>(FragmentNativeLargeSampleBinding::inflate) {

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

    private val admobNative by lazy { AdmobNative() }
    private val adsRepository by lazy { AdsRepository() }

    override fun onViewCreated() {
        loadAds()
    }

    private fun loadAds() {
        admobNative.loadNativeAds(
            activity,
            binding.adsPlaceHolder,
            adsControl = adsRepository.getAdConfig(
                nativeAd,
                sharedPrefManager.isAppPurchased,
                internetManager.isInternetConnected
            ),
            LayoutType.LARGE,
            object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {}
                override fun onAdLoaded() {}
                override fun onAdImpression() {}
                override fun onPreloaded() {}
            }
        )
    }
}