package com.orbitalsonic.adsserverbase.common.firebase

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.orbitalsonic.adsserverbase.adsconfig.utils.repository.AdsRepository
import com.orbitalsonic.adsserverbase.common.firebase.RemoteConstants.ADS_CONFIGURATIONS_KEY
import com.orbitalsonic.adsserverbase.common.network.InternetManager

class RemoteConfiguration(
    private val internetManager: InternetManager) {

    private val configTag = "TAG_REMOTE_CONFIG"

    fun checkRemoteConfig(callback: (fetchSuccessfully: Boolean) -> Unit) {
        if (internetManager.isInternetConnected) {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 2 }
            remoteConfig.setConfigSettingsAsync(configSettings)
            fetchRemoteValues(callback)
        } else {
            Log.d(configTag, "checkRemoteConfig: Internet Not Found!")
            callback.invoke(false)
        }
    }

    private fun fetchRemoteValues(callback: (fetchSuccessfully: Boolean) -> Unit) {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    updateRemoteValues(callback)
                } catch (ex: Exception) {
                    Log.d(configTag, "fetchRemoteValues: ${it.exception}")
                    callback.invoke(false)
                }
            } else {
                Log.d(configTag, "fetchRemoteValues: ${it.exception}")
                callback.invoke(false)
            }
        }.addOnFailureListener {
            Log.d(configTag, "fetchRemoteValues: ${it.message}")
            callback.invoke(false)
        }

    }

    @Throws(Exception::class)
    private fun updateRemoteValues(callback: (fetchSuccessfully: Boolean) -> Unit) {
        val remoteConfig = Firebase.remoteConfig

        AdsRepository().setAdsConfiguration(remoteConfig[ADS_CONFIGURATIONS_KEY].asString())
        Log.d(configTag, "checkRemoteConfig: Fetched Successfully")
        callback.invoke(true)
    }
}