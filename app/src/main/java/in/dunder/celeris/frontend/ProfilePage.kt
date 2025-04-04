package `in`.dunder.celeris.frontend

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import `in`.dunder.celeris.db.DatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentProfilePageBinding
import `in`.dunder.celeris.utils.QRCodeUtils

class ProfilePage : Fragment() {
    private lateinit var binding: FragmentProfilePageBinding
    private var dbHelper: DatabaseHelper? = null
    private var qrCodeImageView: ImageView? = null
    private val merchantId = "13"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePageBinding.inflate(layoutInflater, container, false)
        dbHelper = DatabaseHelper(requireActivity())
        qrCodeImageView = binding.qrCode

        var qrCode: Bitmap? = dbHelper?.getQRCode(merchantId)
        if (qrCode == null) {
            qrCode = QRCodeUtils.generateQRCode(merchantId)
            dbHelper?.saveQRCode(merchantId, qrCode)
        }

        qrCodeImageView?.setImageBitmap(qrCode)

        return binding.root
    }
}