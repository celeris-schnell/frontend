package `in`.dunder.celeris.frontend

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.R
import `in`.dunder.celeris.frontend.databinding.ActivitySecureBinding
import `in`.dunder.celeris.utils.NetworkMonitor
import androidx.navigation.NavController
import android.view.View
import android.graphics.Color
import android.view.WindowManager

class SecureActivity : AppCompatActivity() {
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var binding: ActivitySecureBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Remove status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivitySecureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.homeButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homePage) {
                navController.navigate(R.id.homePage)
                updateBottomBarSelection(true)
            }
        }

        binding.profileButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.profilePage) {
                navController.navigate(R.id.profilePage)
                updateBottomBarSelection(false)
            }
        }

        // Set initial selection
        updateBottomBarSelection(navController.currentDestination?.id == R.id.homePage)

        // Check authentication
        AuthDatabaseHelper(this).apply {
            if (!isUserLoggedIn()) {
                finish()
            }
        }

        // Setup network monitoring
        networkMonitor = NetworkMonitor(this@SecureActivity) { isOnline, justCameOnline ->
            runOnUiThread {
                if (justCameOnline) {
                    callApi()
                }
            }
        }

        // Set up navigation listener to update bottom bar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomBarSelection(destination.id == R.id.homePage)
        }
    }

    private fun updateBottomBarSelection(isHomeSelected: Boolean) {
        // Update icons and text colors
        binding.homeIcon.setColorFilter(
            if (isHomeSelected) getColor(R.color.primary) else getColor(R.color.text_primary)
        )
        binding.profileIcon.setColorFilter(
            if (!isHomeSelected) getColor(R.color.primary) else getColor(R.color.text_primary)
        )

        binding.homeText.setTextColor(
            if (isHomeSelected) getColor(R.color.primary) else getColor(R.color.text_primary)
        )
        binding.profileText.setTextColor(
            if (!isHomeSelected) getColor(R.color.primary) else getColor(R.color.text_primary)
        )
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