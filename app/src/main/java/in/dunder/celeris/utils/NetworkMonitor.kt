package `in`.dunder.celeris.utils

import android.content.Context
import android.net.*

class NetworkMonitor(
    context: Context,
    private val onNetworkChange: (isOnline: Boolean, justCameOnline: Boolean) -> Unit
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var wasOnline = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val isOnline = isCurrentlyOnline()
            if (!wasOnline && isOnline) {
                onNetworkChange(true, true)
            } else {
                onNetworkChange(true, false)
            }
            wasOnline = true
        }

        override fun onLost(network: Network) {
            onNetworkChange(false, false)
            wasOnline = false
        }
    }

    fun start() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        wasOnline = isCurrentlyOnline()
    }

    fun stop() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) {
        }
    }

    fun isCurrentlyOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
