package com.omer.cloudrepo.resolvers

import com.lagradost.cloudstream3.app
import java.net.HttpURLConnection
import java.net.URL

object DynamicUrlResolver {
    private const val CONFIG_URL = "https://raw.githubusercontent.com/omer-dogann/cloudrepo/main/domain_resolvers.json"
    private val cache = mutableMapOf<String, String>()

    suspend fun getActiveUrl(providerName: String, defaultUrl: String): String {
        if (cache.containsKey(providerName)) {
            return cache[providerName]!!
        }

        try {
            // Check if default is responsive
            val res = app.get(defaultUrl, timeout = 3)
            if (res.isSuccessful) {
                cache[providerName] = defaultUrl
                return defaultUrl
            }
        } catch (e: Exception) {
            // Default URL blocked or down, attempt resolution
        }

        // Return fallback or default
        cache[providerName] = defaultUrl
        return defaultUrl
    }
}
