package com.orbitalsonic.adsserverbase.adsconfig.utils.repository

import android.util.Log
import com.google.gson.Gson
import com.orbitalsonic.adsserverbase.adsconfig.utils.constants.AdsConstants.adsConfiguration
import com.orbitalsonic.adsserverbase.adsconfig.utils.models.Ads
import com.orbitalsonic.adsserverbase.adsconfig.utils.models.AdsConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class AdsRepository() {
    private var configJob: Job? = null

    fun setAdsConfiguration(jsonData: String?) {
        configJob?.cancel() // cancel any previous work
        configJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val adsConfigJson = jsonObject.optJSONObject("AdsConfig")
                    if (adsConfigJson != null) {
                        adsConfiguration = Gson().fromJson(adsConfigJson.toString(), AdsConfig::class.java)
                        Log.i("AdsResponse", "$adsConfigJson")
                        Log.i("AdsResponse", "$adsConfiguration")
                    } else {
                        Log.w("AdsResponse", "AdsConfig is missing in the JSON")
                    }
                }
            } catch (ex: JSONException) {
                Log.e("AdsResponse", "JSON Parsing Error: $ex")
            }
        }
    }


    fun getAdConfig(adsType: String,isAppPurchased: Boolean,isInternetConnected: Boolean): Ads? {
        val ads = adsConfiguration?.ads?.find { it.type == adsType }
        if (ads != null){
            ads.isAppPurchased = isAppPurchased
            ads.isInternetConnected = isInternetConnected
        }
        return ads
    }
}
