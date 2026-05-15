package com.example.namma_hasiru.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.namma_hasiru.R
import com.example.namma_hasiru.data.TreeEntry
import com.example.namma_hasiru.databinding.FragmentMapBinding
import com.example.namma_hasiru.utils.LocationHelper
import com.example.namma_hasiru.viewmodel.TreeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels to share data state across fragments
    private val viewModel: TreeViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var locationHelper: LocationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        locationHelper = LocationHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.fabAddPlant.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_logPlantFragment)
        }

        binding.btnListView.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_treeListFragment)
        }

        viewModel.survivalRate.observe(viewLifecycleOwner) { rate ->
            binding.tvSurvivalRate.text = "Survival Rate: ${rate?.toInt() ?: 0}%"
        }

        viewModel.allTrees.observe(viewLifecycleOwner) { trees ->
            Log.d("MapFragment", "DB Update: Found ${trees.size} trees")
            updateMarkers(trees)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }

        map.setOnInfoWindowClickListener { marker ->
            val tree = marker.tag as? TreeEntry
            tree?.let {
                val action = MapFragmentDirections.actionMapFragmentToTreeDetailsFragment(it.id)
                findNavController().navigate(action)
            }
        }
        
        viewModel.allTrees.value?.let { updateMarkers(it) }
    }

    private fun updateMarkers(trees: List<TreeEntry>) {
        val map = googleMap ?: return
        map.clear()
        
        if (trees.isEmpty()) {
            centerOnUser()
            return
        }

        val builder = LatLngBounds.Builder()
        var validPointsCount = 0

        trees.forEach { tree ->
            if (tree.latitude != 0.0 || tree.longitude != 0.0) {
                val position = LatLng(tree.latitude, tree.longitude)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(tree.speciesName)
                        .snippet("Status: ${tree.healthStatus}")
                )
                marker?.tag = tree
                builder.include(position)
                validPointsCount++
            }
        }
        
        if (validPointsCount > 0) {
            try {
                val bounds = builder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            } catch (e: Exception) {
                val firstValid = trees.firstOrNull { it.latitude != 0.0 }
                firstValid?.let {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
                }
            }
        } else {
            centerOnUser()
        }
    }

    private fun centerOnUser() {
        locationHelper.getCurrentLocation { location ->
            location?.let {
                val userPos = LatLng(it.latitude, it.longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userPos, 14f))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        _binding?.mapView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        _binding?.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        _binding?.mapView?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.mapView?.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        _binding?.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.mapView?.onSaveInstanceState(outState)
    }
}
