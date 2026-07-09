package com.example.config

import com.example.BuildConfig

object BackendConfig {
    // These values are now managed via .env file and BuildConfig
    const val DEFAULT_BACKEND_URL = BuildConfig.BACKEND_URL
    const val PRODUCTION_BACKEND_URL = "https://your-backend-domain.com/"

    var currentBackendUrl: String = DEFAULT_BACKEND_URL
        private set

    fun setBackendUrl(url: String) {
        currentBackendUrl = if (url.endsWith("/")) url else "$url/"
    }

    fun isProductionMode(): Boolean = currentBackendUrl == PRODUCTION_BACKEND_URL

    fun reset() {
        currentBackendUrl = DEFAULT_BACKEND_URL
    }
}
