package `in`.dunder.celeris.frontend

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.databinding.ActivitySecureBinding

class SecureActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var binding: ActivitySecureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySecureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AuthDatabaseHelper(this).apply {
            if (!isUserLoggedIn()) {
                finish()
            } else {
                Toast.makeText(this@SecureActivity, user.id.toString(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@SecureActivity, user.email, Toast.LENGTH_SHORT).show()
                Toast.makeText(this@SecureActivity, user.name, Toast.LENGTH_SHORT).show()
                Toast.makeText(this@SecureActivity, user.phoneNumber, Toast.LENGTH_SHORT).show()
                Toast.makeText(this@SecureActivity, user.balance.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    updateNetworkStatus(true)
                }
            }

            override fun onLost(network: Network) {
                runOnUiThread {
                    updateNetworkStatus(false)
                }
            }
        }

        checkNetworkStatus()
    }

    override fun onResume() {
        super.onResume()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onPause() {
        super.onPause()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: IllegalArgumentException) {
            Log.e("SecureActivity", "Network callback was not registered or already unregistered", e)
        }
    }

    private fun checkNetworkStatus() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        updateNetworkStatus(isConnected)
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        binding.statusTextView.text = if (isConnected) "You are online" else "You are offline"
    }
}