package `in`.dunder.celeris.frontend

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.camera.view.PreviewView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.PlanarYUVLuminanceSource
import `in`.dunder.celeris.utils.dataStore
import kotlinx.coroutines.launch

// Extension function to convert ImageProxy to ZXing's PlanarYUVLuminanceSource.
private fun ImageProxy.toLuminanceSource(): PlanarYUVLuminanceSource? {
    // Ensure the image is in YUV_420_888 format (default for CameraX)
    if (format != ImageFormat.YUV_420_888) {
        Log.e("LuminanceSource", "Unsupported image format: $format")
        return null
    }

    // Get the Y plane (luminance channel)
    val yBuffer = planes[0].buffer // Y channel is the first plane in YUV_420_888 format.
    val ySize = yBuffer.remaining()
    val yData = ByteArray(ySize)
    yBuffer.get(yData)

    // Get image dimensions
    val width = width
    val height = height

    // Create PlanarYUVLuminanceSource (ZXing class)
    return PlanarYUVLuminanceSource(
        yData,          // Y channel data
        width,          // Image width
        height,         // Image height
        0,              // Left crop (0 for full width)
        0,              // Top crop (0 for full height)
        width,          // Cropped width
        height,         // Cropped height,
        false           // No rotation needed.
    )
}

class ScanPayFragment : Fragment() {

    private lateinit var previewView: PreviewView

    private var cameraProvider: ProcessCameraProvider? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "ScanPayFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan_pay, container, false)

        // Initialize camera preview view
        previewView = view.findViewById(R.id.previewView)

        // Check permissions and start camera if granted
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        return view
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // Unbind all use cases before rebinding to avoid conflicts.
            cameraProvider?.unbindAll()

            // Preview use case for live camera feed.
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // Image analysis use case for QR code scanning.
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), QRCodeAnalyzer { qrResult ->
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Scanned QR Code: $qrResult",
                            Toast.LENGTH_LONG
                        ).show()

                        lifecycleScope.launch {
                            saveClientIdToDataStore(qrResult)

//                            findNavController().navigate(R.id.action_scanPayFragment_to_sendMoney)
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                .navigate(R.id.action_scanPayFragment_to_sendMoney)
                        }
                    }
                }) }



            try {
                // Bind use cases to lifecycle.
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private suspend fun saveClientIdToDataStore(clientId: String) {
        val clientIdKey = stringPreferencesKey("client_id")
        requireContext().dataStore.edit { preferences ->
            preferences[clientIdKey] = clientId
        }
    }


    private class QRCodeAnalyzer(private val onQRCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

        override fun analyze(imageProxy: ImageProxy) {
            try {
                val luminanceSource = imageProxy.toLuminanceSource() ?: return // Convert image to LuminanceSource.
                val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))

                try {
                    val result: Result = MultiFormatReader().decode(binaryBitmap) // Decode QR code.
                    onQRCodeScanned(result.text)
                } catch (e: Exception) {
                    Log.d("QRCodeAnalyzer", "No QR code found in this frame.")
                }

            } finally {
                imageProxy.close() // Always close after processing.
            }
        }
    }
}




