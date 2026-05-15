package com.example.namma_hasiru.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.namma_hasiru.databinding.FragmentSpeciesGuideBinding

class SpeciesGuideFragment : Fragment() {

    private var _binding: FragmentSpeciesGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeciesGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.tvGuideContent.text = """
            🌱 Recommended for your region:
            
            1. Neem (Azadirachta indica)
               - High survival rate in dry climates.
               - Medicinal properties.
            
            2. Honge (Pongamia pinnata)
               - Native to Karnataka.
               - Excellent for nitrogen fixation.
            
            3. Jackfruit (Artocarpus heterophyllus)
               - Great for sustainability and food security.
               - Thrives in tropical areas.
            
            💡 Tip: Native species have a 30% higher survival rate than exotic ones!
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
