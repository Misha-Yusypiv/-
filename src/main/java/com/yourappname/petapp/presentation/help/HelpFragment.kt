package com.yourappname.petapp.presentation.help

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yourappname.petapp.databinding.FragmentHelpBinding
import com.yourappname.petapp.data.firebase.FirebaseSource
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private var photoUri: Uri? = null
    private var location: Location? = null
    private var feedbackChecked: Boolean = true
    private lateinit var firebaseSource: FirebaseSource

    private val requestCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val uri = bitmapToUri(it)
                photoUri = uri
                binding.btnUploadPhoto.setImageURI(uri)
            }
        }
    }

    private val requestGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoUri = it
            binding.btnUploadPhoto.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseSource = (requireActivity().application as com.yourappname.petapp.App).firebaseSource
        setupUI()
    }

    private fun setupUI() {
        // Photo upload
        binding.btnUploadPhoto.setOnClickListener {
            showPhotoPicker()
        }
        // Geolocation
        binding.btnAddLocation.setOnClickListener {
            requestLocation()
        }
        // Feedback checkbox
        binding.llFeedback.setOnClickListener {
            feedbackChecked = !feedbackChecked
            binding.llFeedback.getChildAt(1).visibility = if (feedbackChecked) View.VISIBLE else View.INVISIBLE
        }
        // Submit
        binding.btnSubmitHelp.setOnClickListener {
            submitHelpRequest()
        }
    }

    private fun showPhotoPicker() {
        val options = arrayOf("Камера", "Галерея")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Завантажити фото")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        requestCameraLauncher.launch(intent)
    }

    private fun openGallery() {
        requestGalleryLauncher.launch("image/*")
    }

    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        val locationManager = requireContext().getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
        val provider = android.location.LocationManager.GPS_PROVIDER
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val loc = locationManager.getLastKnownLocation(provider)
            if (loc != null) {
                location = loc
                Toast.makeText(requireContext(), "Геолокація додана", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Не вдалося отримати геолокацію", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadPhotoToFirebase(uri: Uri): String {
        return when (val result = firebaseSource.uploadHelpPhoto(uri)) {
            is Result.Success -> result.getOrNull() ?: ""
            is Result.Failure -> throw result.exceptionOrNull() ?: Exception("Unknown error")
        }
    }

    private fun submitHelpRequest() {
        val subject = binding.etHelpSubject.text.toString().trim()
        val description = binding.etHelpDescription.text.toString().trim()
        if (subject.isEmpty()) {
            Toast.makeText(requireContext(), "Введіть назву звернення", Toast.LENGTH_SHORT).show()
            return
        }
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Введіть опис звернення", Toast.LENGTH_SHORT).show()
            return
        }
        binding.btnSubmitHelp.isEnabled = false
        binding.btnSubmitHelp.text = "Завантаження..."
        lifecycleScope.launch {
            try {
                val photoUrl = photoUri?.let { uploadPhotoToFirebase(it) }
                val userId = firebaseSource.getCurrentUser()?.uid
                val result = firebaseSource.saveHelpRequest(
                    subject = subject,
                    description = description,
                    photoUrl = photoUrl,
                    location = location,
                    feedback = feedbackChecked,
                    userId = userId
                )
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Звернення надіслано!", Toast.LENGTH_LONG).show()
                    binding.etHelpSubject.text?.clear()
                    binding.etHelpDescription.text?.clear()
                    photoUri = null
                    binding.btnUploadPhoto.setImageResource(com.yourappname.petapp.R.drawable.ic_camera)
                    location = null
                } else {
                    throw result.exceptionOrNull() ?: Exception("Unknown error")
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Помилка: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnSubmitHelp.isEnabled = true
                binding.btnSubmitHelp.text = "Надіслати"
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "Temp", null)
        return Uri.parse(path)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 