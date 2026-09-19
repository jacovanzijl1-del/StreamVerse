package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("streamverse_prefs", Context.MODE_PRIVATE)

    var tmdbApiKey: String
        get() {
            val custom = prefs.getString("tmdb_api_key", "") ?: ""
            if (custom.isNotBlank()) return custom
            val buildConfigKey = BuildConfig.TMDB_API_KEY
            return if (buildConfigKey.isNotBlank() && buildConfigKey != "DEFAULT_TMDB_API_KEY") buildConfigKey else ""
        }
        set(value) = prefs.edit().putString("tmdb_api_key", value.trim()).apply()

    var simklClientId: String
        get() {
            val custom = prefs.getString("simkl_client_id", "") ?: ""
            if (custom.isNotBlank()) return custom
            val buildConfigKey = runCatching { BuildConfig.SIMKL_CLIENT_ID }.getOrDefault("")
            return if (buildConfigKey.isNotBlank() && buildConfigKey != "DEFAULT_SIMKL_CLIENT_ID") buildConfigKey else ""
        }
        set(value) = prefs.edit().putString("simkl_client_id", value.trim()).apply()

    var simklClientSecret: String
        get() {
            val custom = prefs.getString("simkl_client_secret", "") ?: ""
            if (custom.isNotBlank()) return custom
            val buildConfigKey = runCatching { BuildConfig.SIMKL_CLIENT_SECRET }.getOrDefault("")
            return if (buildConfigKey.isNotBlank() && buildConfigKey != "DEFAULT_SIMKL_CLIENT_SECRET") buildConfigKey else ""
        }
        set(value) = prefs.edit().putString("simkl_client_secret", value.trim()).apply()

    var orionApiKey: String
        get() {
            val custom = prefs.getString("orion_api_key", "") ?: ""
            if (custom.isNotBlank()) return custom
            val buildConfigKey = BuildConfig.ORION_API_KEY
            if (buildConfigKey.isNotBlank() && buildConfigKey != "DEFAULT_ORION_API_KEY") return buildConfigKey
            return "DNC6KJL6LVFBFSBJEUM96EHDDNJJUU6N"
        }
        set(value) = prefs.edit().putString("orion_api_key", value.trim()).apply()

    var orionAppKey: String
        get() = prefs.getString("orion_app_key", "TESTTESTTESTTESTTESTTESTTESTTEST") ?: "TESTTESTTESTTESTTESTTESTTESTTEST"
        set(value) = prefs.edit().putString("orion_app_key", value.trim()).apply()

    var watchRegion: String
        get() = prefs.getString("watch_region", "US") ?: "US"
        set(value) = prefs.edit().putString("watch_region", value).apply()

    var autoPlayNextEpisode: Boolean
        get() = prefs.getBoolean("autoplay_next", true)
        set(value) = prefs.edit().putBoolean("autoplay_next", value).apply()

    var defaultQuality: String
        get() = prefs.getString("default_quality", "1080P") ?: "1080P"
        set(value) = prefs.edit().putString("default_quality", value).apply()
}
