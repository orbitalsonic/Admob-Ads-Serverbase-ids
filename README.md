# Admob Ads Server ids

This repository demonstrates the integration of multiple AdMob ad types in an Android app, with ad unit IDs and enable/disable flags fetched remotely via Firebase Remote Config.

## ✨ Supported Ad Types

- ✅ **Banner Ad**
- ✅ **Collapsible Banner Ad**
- ✅ **Native Ad**
- ✅ **Interstitial Ad**
- ✅ **Rewarded Ad**
- ✅ **Rewarded Interstitial Ad**
- ✅ **App Open Ad**

## 🔧 How It Works

The ad configuration is fetched from Firebase Remote Config in the following JSON format:

```json
{
  "AdsConfig": {
    "Ads": [
      {
        "type": "banner_ad",
        "isEnabled": true,
        "adID": "ca-app-pub-3940256099942544/2014213617"
      },
      {
        "type": "native_ad",
        "isEnabled": true,
        "adID": "ca-app-pub-3940256099942544/2247696110"
      },
      {
        "type": "inter_ad",
        "isEnabled": true,
        "adID": "ca-app-pub-3940256099942544/1033173712"
      },
      {
        "type": "rewarded_ad",
        "isEnabled": true,
        "adID": "ca-app-pub-3940256099942544/5224354917"
      },
      {
        "type": "rewarded_inter_ad",
        "isEnabled": true,
        "adID": "ca-app-pub-3940256099942544/5354046379"
      },
      {
        "type": "app_open_ad",
        "isEnabled": true,
        "adID": "ca-app-pub-3940256099942544/3419835294"
      }
    ]
  }
}
```

This allows dynamic control over ad visibility and unit IDs without publishing a new version of your app.

## 📦 Features

- Clean and modular architecture
- Remote control of ad visibility
- Fail-safe ad handling
- Support for both XML and Jetpack Compose integration (optional)

## 🛡️ ProGuard Configuration (Important for Release Builds)

To ensure correct serialization/deserialization of ad config using Gson, **add the following ProGuard rules**:

```pro
# Gson + Model Class Configuration
-keepattributes Signature
-keepattributes *Annotation*

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
```

Also, if your ad model implements `Parcelable` or `Serializable`, add:

```pro
-keepclassmembers class * implements java.io.Serializable { *; }
-keepclassmembers class * implements android.os.Parcelable { *; }
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable
```

## 🚀 Getting Started

1. Clone this repository.
2. Add your Firebase Remote Config setup.
3. Ensure you have internet permissions in your `AndroidManifest.xml`.
4. Plug in your actual AdMob IDs in Firebase and test.

## 📄 License

Copyright 2022 OrbitalSonic

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

---

> **Note:** Always test ads using test IDs during development. Only use real IDs in production.
