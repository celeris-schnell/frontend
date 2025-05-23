package `in`.dunder.celeris.frontend

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.db.DatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentProfilePageBinding
import `in`.dunder.celeris.utils.QRCodeUtils

class ProfilePage : Fragment() {
    private lateinit var binding: FragmentProfilePageBinding
    private var dbHelper: DatabaseHelper? = null
    private var authDbHelper: AuthDatabaseHelper? = null // Added AuthDatabaseHelper
    private var qrCodeImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePageBinding.inflate(layoutInflater, container, false)
        dbHelper = DatabaseHelper(requireActivity())
        authDbHelper = AuthDatabaseHelper(requireActivity()) // Initialize AuthDatabaseHelper
        qrCodeImageView = binding.qrCode
        AuthDatabaseHelper(requireContext()).apply{
            binding.username.text=user.name
            binding.contact.text = user.phoneNumber
            binding.userid.text=user.id.toString()
//            binding.qrCode.setImageBitmap(dbHelper?.getQRCode(user.id.toString()))
        }

        var qrCode: Bitmap? = dbHelper?.getQRCode(binding.userid.text.toString())
        if (qrCode == null) {
            qrCode = QRCodeUtils.generateQRCode(binding.userid.text.toString())
            dbHelper?.saveQRCode(binding.userid.text.toString(), qrCode)
        }

        qrCodeImageView?.setImageBitmap(qrCode)

        // Set up logout button click listener
        binding.logoutButton.setOnClickListener {
            performLogout()
        }

        return binding.root
    }

    // New function to handle the logout process
    private fun performLogout() {
        // Clear the user data from the database
        authDbHelper?.logoutUser()

        // Navigate to the login screen
        navigateToLogin()
    }

    // New function to handle navigation to login page
    private fun navigateToLogin() {
        // Navigate to the AuthActivity which contains the login fragment
        val intent = Intent(activity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
