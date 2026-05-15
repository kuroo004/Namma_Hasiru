package com.example.namma_hasiru.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.namma_hasiru.databinding.FragmentStatusUpdateBinding
import com.example.namma_hasiru.viewmodel.TreeViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class StatusUpdateFragment : Fragment() {

    private var _binding: FragmentStatusUpdateBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels to share data state across fragments
    private val viewModel: TreeViewModel by activityViewModels()
    private val args: StatusUpdateFragmentArgs by navArgs()
    
    private var tempPhotoUri: Uri? = null
    private var updatedPhotoPath: String? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivGrowthPhoto)
                savePhotoToInternalStorage()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTakeGrowthPhoto.setOnClickListener {
            prepareAndTakePhoto()
        }

        binding.btnUpdateStatus.setOnClickListener {
            updateStatus()
        }
    }

    private fun prepareAndTakePhoto() {
        val photoFile = File(requireContext().cacheDir, "temp_growth.jpg")
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
            val fileName = "growth_${System.currentTimeMillis()}.jpg"
            val photoFile = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(photoFile)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            updatedPhotoPath = photoFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateStatus() {
        val selectedStatus = when (binding.rgHealthStatus.checkedRadioButtonId) {
            binding.rbSprouted.id -> "Sprouted"
            binding.rbGrowing.id -> "Growing"
            binding.rbHealthy.id -> "Healthy"
            binding.rbDied.id -> "Died"
            else -> null
        }

        if (selectedStatus == null) {
            Toast.makeText(requireContext(), "Please select a health status", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val tree = viewModel.getTreeById(args.treeId)
            tree?.let {
                val updatedTree = it.copy(
                    healthStatus = selectedStatus,
                    lastCheckUpDate = System.currentTimeMillis(),
                    day1PhotoUri = updatedPhotoPath ?: it.day1PhotoUri
                )
                viewModel.update(updatedTree)
                Toast.makeText(requireContext(), "Status updated! Keep up the mission.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
