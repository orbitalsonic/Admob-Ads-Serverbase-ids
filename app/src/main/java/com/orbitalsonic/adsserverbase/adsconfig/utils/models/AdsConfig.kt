package com.orbitalsonic.adsserverbase.adsconfig.utils.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdsConfig(
    @SerializedName("Ads") var ads: ArrayList<Ads> = arrayListOf()
): Parcelable