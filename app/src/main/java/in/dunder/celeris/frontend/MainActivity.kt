package `in`.dunder.celeris.frontend

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.dunder.celeris.db.DatabaseHelper
import `in`.dunder.celeris.utils.QRCodeUtils

class MainActivity : AppCompatActivity() {
    private var dbHelper: DatabaseHelper? = null
    private var qrCodeImageView: ImageView? = null
    private val merchantId = "13"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        qrCodeImageView = findViewById(R.id.qr_code_image_view)

        var qrCode: Bitmap? = dbHelper?.getQRCode(merchantId)
        if (qrCode == null) {
            qrCode = QRCodeUtils.generateQRCode(merchantId)
            dbHelper?.saveQRCode(merchantId, qrCode)
        }

        qrCodeImageView?.setImageBitmap(qrCode)
    }
}
