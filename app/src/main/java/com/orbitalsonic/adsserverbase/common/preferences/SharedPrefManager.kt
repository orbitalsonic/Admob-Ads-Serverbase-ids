package com.orbitalsonic.adsserverbase.common.preferences

import android.content.SharedPreferences

private const val FIRST_TIME_ENTRANCE_KEY = "first_time_entrance"
private const val APP_LANGUAGE_CODE_KEY = "app_language_code"
private const val APP_PURCHASED_KEY = "app_purchased"

class SharedPrefManager(private val sharedPreferences: SharedPreferences) {

    var isFirstTimeEntrance: Boolean
        get() = sharedPreferences.getBoolean(FIRST_TIME_ENTRANCE_KEY, true)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(FIRST_TIME_ENTRANCE_KEY, value)
                apply()
            }
        }

    var appLanguageCode: String
        get() = sharedPreferences.getString(APP_LANGUAGE_CODE_KEY, "en") ?: "en"
        set(value) {
            sharedPreferences.edit().apply {
                putString(APP_LANGUAGE_CODE_KEY, value)
                apply()
            }
        }

    var isAppPurchased: Boolean
        get() = sharedPreferences.getBoolean(APP_PURCHASED_KEY, false)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(APP_PURCHASED_KEY, value)
                apply()
            }
        }

}