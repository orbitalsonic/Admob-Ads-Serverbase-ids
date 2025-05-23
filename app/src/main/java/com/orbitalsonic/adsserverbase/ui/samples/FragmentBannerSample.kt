package com.orbitalsonic.adsserverbase.ui.samples

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.orbitalsonic.adsserverbase.adsconfig.banners.AdmobBanner
import com.orbitalsonic.adsserverbase.adsconfig.banners.enums.BannerType
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsProviderType.bannerAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.network.InternetManager
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.FragmentBannerSampleBinding
import com.orbitalsonic.adsserverbase.ui.base.fragments.BaseFragment

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class FragmentBannerSample :
    BaseFragment<FragmentBannerSampleBinding>(FragmentBannerSampleBinding::inflate) {

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

    private val admobBanner by lazy { AdmobBanner() }
    private val adsRepository by lazy { AdsRepository() }

    override fun onViewCreated() {
        loadAds()
    }

    private fun loadAds() {
        admobBanner.loadBannerAds(
            activity = activity,
            binding.adsBannerPlaceHolder,
            adsControl = adsRepository.getAdConfig(
                bannerAd,
                sharedPrefManager.isAppPurchased,
                internetManager.isInternetConnected
            ),
            BannerType.ADAPTIVE_BANNER
        )
    }

    override fun onPause() {
        admobBanner.bannerOnPause()
        super.onPause()
    }

    override fun onResume() {
        admobBanner.bannerOnResume()
        super.onResume()
    }

    override fun onDestroy() {
        admobBanner.bannerOnDestroy()
        super.onDestroy()
    }
}