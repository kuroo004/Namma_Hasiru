package com.example.namma_hasiru.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.namma_hasiru.databinding.FragmentTreeDetailsBinding
import com.example.namma_hasiru.viewmodel.TreeViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TreeDetailsFragment : Fragment() {

    private var _binding: FragmentTreeDetailsBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels to share data state across fragments
    private val viewModel: TreeViewModel by activityViewModels()
    private val args: TreeDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val tree = viewModel.getTreeById(args.treeId)
            tree?.let { entry ->
                _binding?.let { b ->
                    b.tvDetailSpecies.text = entry.speciesName
                    b.tvDetailStatus.text = "Status: ${entry.healthStatus}"
                    b.tvDetailLocation.text = "Location: ${entry.latitude}, ${entry.longitude}"
                    
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    b.tvDetailDate.text = "Planted on: ${sdf.format(Date(entry.datePlanted))}"

                    if (entry.day1PhotoUri.isNotEmpty()) {
                        Glide.with(requireContext())
                            .load(File(entry.day1PhotoUri))
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(b.ivDetailPhoto)
                    }

                    b.btnUpdateTreeStatus.setOnClickListener {
                        val action = TreeDetailsFragmentDirections.actionTreeDetailsFragmentToStatusUpdateFragment(entry.id)
                        findNavController().navigate(action)
                    }

                    b.btnDeleteTree.setOnClickListener {
                        showDeleteConfirmation(entry)
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmation(tree: com.example.namma_hasiru.data.TreeEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Sapling")
            .setMessage("Are you sure you want to remove this ${tree.speciesName} from your mission history?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.delete(tree)
                Toast.makeText(context, "Removed from mission", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
