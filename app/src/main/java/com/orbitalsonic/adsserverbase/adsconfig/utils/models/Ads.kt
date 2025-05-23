package com.orbitalsonic.adsserverbase.adsconfig.utils.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ads(
    @SerializedName("type") val type: String,
    @SerializedName("isEnabled") val isEnabled: Boolean,
    @SerializedName("adID") val adID: String,
    var isAppPurchased: Boolean = false,
    var isInternetConnected: Boolean = true,
): Parcelable