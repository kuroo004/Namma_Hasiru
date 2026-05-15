package com.example.namma_hasiru.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.namma_hasiru.databinding.FragmentLogPlantBinding
import com.example.namma_hasiru.data.TreeEntry
import com.example.namma_hasiru.utils.LocationHelper
import com.example.namma_hasiru.viewmodel.TreeViewModel
import java.io.File
import java.io.FileOutputStream

class LogPlantFragment : Fragment() {

    private var _binding: FragmentLogPlantBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel across fragments
    private val viewModel: TreeViewModel by activityViewModels()
    private lateinit var locationHelper: LocationHelper
    
    private var tempPhotoUri: Uri? = null
    private var savedPhotoPath: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchLocation()
        } else {
            binding.tvLocationStatus.text = "Permission denied. Location required."
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            binding.ivPlantPhoto.setImageURI(tempPhotoUri)
            savePhotoToInternalStorage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogPlantBinding.inflate(inflater, container, false)
        locationHelper = LocationHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSavePlant.isEnabled = false 
        checkPermissionsAndFetchLocation()

        binding.btnTakePhoto.setOnClickListener {
            prepareAndTakePhoto()
        }

        binding.btnSavePlant.setOnClickListener {
            savePlant()
        }
    }

    private fun checkPermissionsAndFetchLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchLocation()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun fetchLocation() {
        binding.tvLocationStatus.text = "Fetching precise location..."
        locationHelper.getCurrentLocation { location ->
            if (isAdded) {
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    binding.tvLocationStatus.text = "Location tagged: ${String.format("%.4f", it.latitude)}, ${String.format("%.4f", it.longitude)}"
                    checkReadyToSave()
                } ?: run {
                    binding.tvLocationStatus.text = "Failed to get location. Try enabling GPS."
                }
            }
        }
    }

    private fun prepareAndTakePhoto() {
        val photoFile = File(requireContext().cacheDir, "temp_plant.jpg")
        tempPhotoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )
        takePictureLauncher.launch(tempPhotoUri!!)
    }

    private fun savePhotoToInternalStorage() {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(tempPhotoUri!!)
            val fileName = "plant_${System.currentTimeMillis()}.jpg"
            val photoFile = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(photoFile)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            savedPhotoPath = photoFile.absolutePath
            checkReadyToSave()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkReadyToSave() {
        binding.btnSavePlant.isEnabled = savedPhotoPath != null && latitude != null
    }

    private fun savePlant() {
        val species = binding.etSpeciesName.text.toString()
        val lat = latitude
        val lon = longitude
        val path = savedPhotoPath

        if (species.isBlank()) {
            Toast.makeText(context, "Please enter a species name", Toast.LENGTH_SHORT).show()
            return
        }
        if (lat == null || lon == null || path == null) {
            Toast.makeText(context, "Location or photo missing", Toast.LENGTH_SHORT).show()
            return
        }

        val newTree = TreeEntry(
            speciesName = species,
            latitude = lat,
            longitude = lon,
            day1PhotoUri = path,
            healthStatus = "Sprouted",
            datePlanted = System.currentTimeMillis()
        )

        viewModel.insert(newTree)
        Toast.makeText(context, "Sapling registered! Showing on map.", Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
