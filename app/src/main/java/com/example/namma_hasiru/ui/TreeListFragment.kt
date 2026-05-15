package com.example.namma_hasiru.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namma_hasiru.databinding.FragmentTreeListBinding
import com.example.namma_hasiru.viewmodel.TreeViewModel

class TreeListFragment : Fragment() {

    private var _binding: FragmentTreeListBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel across fragments
    private val viewModel: TreeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TreeListAdapter { tree ->
            val action = TreeListFragmentDirections.actionTreeListFragmentToTreeDetailsFragment(tree.id)
            findNavController().navigate(action)
        }

        binding.rvTrees.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTrees.adapter = adapter

        viewModel.allTrees.observe(viewLifecycleOwner) { trees ->
            if (trees.isEmpty()) {
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvTrees.visibility = View.GONE
            } else {
                binding.tvEmptyList.visibility = View.GONE
                binding.rvTrees.visibility = View.VISIBLE
                adapter.submitList(trees)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
