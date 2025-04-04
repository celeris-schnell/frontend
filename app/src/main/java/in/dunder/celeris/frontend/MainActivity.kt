//package `in`.dunder.celeris.frontend
//
//import android.graphics.Bitmap
//import android.os.Bundle
//import android.widget.ImageView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import `in`.dunder.celeris.db.DatabaseHelper
//import `in`.dunder.celeris.utils.QRCodeUtils
//
//class MainActivity : AppCompatActivity() {
//    private var dbHelper: DatabaseHelper? = null
//    private var qrCodeImageView: ImageView? = null
//    private val merchantId = "13"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        dbHelper = DatabaseHelper(this)
//        qrCodeImageView = findViewById(R.id.qr_code_image_view)
//
//        var qrCode: Bitmap? = dbHelper?.getQRCode(merchantId)
//        if (qrCode == null) {
//            qrCode = QRCodeUtils.generateQRCode(merchantId)
//            dbHelper?.saveQRCode(merchantId, qrCode)
//        }
//
//        qrCodeImageView?.setImageBitmap(qrCode)
//    }
//}
package `in`.dunder.celeris.frontend

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var networkStatusText: TextView
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextView
        networkStatusText = findViewById(R.id.statusTextView)

        // Initialize ConnectivityManager
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Create NetworkCallback
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

        // Check initial network status
        checkNetworkStatus()
    }

    override fun onResume() {
        super.onResume()
        // Register the callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the callback
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun checkNetworkStatus() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        updateNetworkStatus(isConnected)
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        networkStatusText.text = if (isConnected) "You are online" else "You are offline"
    }
}