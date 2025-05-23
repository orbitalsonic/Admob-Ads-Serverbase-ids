package com.orbitalsonic.adsserverbase.adsconfig.banners.callbacks

/**
 * @Author: Muhammad Yaqoob
 * @Date: 14,March,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */
interface BannerCallBack {
    fun onAdFailedToLoad(adError:String){}
    fun onAdLoaded(){}
    fun onAdImpression(){}
    fun onAdClicked(){}
    fun onAdClosed(){}
    fun onAdOpened(){}
}