package `in`.dunder.celeris.frontend

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.databinding.ActivitySecureBinding
import `in`.dunder.celeris.utils.NetworkMonitor

class SecureActivity : AppCompatActivity() {
    private lateinit var networkMonitor: NetworkMonitor
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

        supportFragmentManager.beginTransaction()
            .replace(R.id.main, HomePage())
            .commit()

        AuthDatabaseHelper(this).apply {
            if (!isUserLoggedIn()) {
                finish()
            }
        }

        networkMonitor = NetworkMonitor(this@SecureActivity) { isOnline, justCameOnline ->
            runOnUiThread {
                if (justCameOnline) {
                    callApi()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.start()
    }

    override fun onPause() {
        super.onPause()
        networkMonitor.stop()
    }

    private fun callApi() {
        Toast.makeText(this@SecureActivity, "Network is back", Toast.LENGTH_LONG).show()
    }
}