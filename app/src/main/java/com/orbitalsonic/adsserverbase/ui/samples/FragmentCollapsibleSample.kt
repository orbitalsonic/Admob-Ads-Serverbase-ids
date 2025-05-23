package com.orbitalsonic.adsserverbase.ui.samples

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.orbitalsonic.adsserverbase.R
import com.orbitalsonic.adsserverbase.adsconfig.banners.AdmobBanner
import com.orbitalsonic.adsserverbase.adsconfig.banners.callbacks.BannerCallBack
import com.orbitalsonic.adsserverbase.adsconfig.banners.enums.BannerType
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsProviderType.bannerAd
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.network.InternetManager
import com.orbitalsonic.adsserverbase.common.observers.SingleLiveEvent
import com.orbitalsonic.adsserverbase.common.preferences.SharedPrefManager
import com.orbitalsonic.adsserverbase.databinding.FragmentCollapsibleSampleBinding
import com.orbitalsonic.adsserverbase.helpers.navigation.popFrom
import com.orbitalsonic.adsserverbase.helpers.ui.goBackPressed
import com.orbitalsonic.adsserverbase.ui.base.fragments.BaseFragment

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
class FragmentCollapsibleSample : BaseFragment<FragmentCollapsibleSampleBinding>(FragmentCollapsibleSampleBinding::inflate) {

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

    private val adsObserver = SingleLiveEvent<Boolean>()
    private var isCollapsibleOpen = false
    private var isBackPressed = false

    override fun onViewCreated() {
        loadAds()
        initObserver()

        goBackPressed {
            onBackPressed()
        }
    }

    private fun initObserver(){
        adsObserver.observe(viewLifecycleOwner){
            if (it){
                onBack()
            }
        }
    }

    private fun onBackPressed() {
        if (isAdded){
            try {
                if (!isBackPressed){
                    isBackPressed = true
                    if (isCollapsibleOpen){
                        admobBanner.bannerOnDestroy()
                        binding.adsBannerPlaceHolder.removeAllViews()
                    }else{
                        onBack()
                    }
                }
            }catch (ex:Exception){
                isBackPressed = false
            }
        }
    }

    private fun onBack(){
        popFrom(R.id.fragmentCollapsibleSample)
    }

    private fun loadAds(){
        admobBanner.loadBannerAds(
            activity,
            binding.adsBannerPlaceHolder,
            adsControl = adsRepository.getAdConfig(
                bannerAd,
                sharedPrefManager.isAppPurchased,
                internetManager.isInternetConnected
            ),
            BannerType.COLLAPSIBLE_BOTTOM,
            object : BannerCallBack {
                override fun onAdClosed() {
                    isCollapsibleOpen = false

                    if (isBackPressed){
                        adsObserver.value = true
                    }
                }

                override fun onAdOpened() {
                    isCollapsibleOpen = true
                }


            }
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