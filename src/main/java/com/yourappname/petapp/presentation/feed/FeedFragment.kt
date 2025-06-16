package com.yourappname.petapp.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yourappname.petapp.R
import com.yourappname.petapp.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBookVeterinary.setOnClickListener {
            findNavController().navigate(R.id.action_feed_to_services)
        }

        binding.btnBookGrooming.setOnClickListener {
            findNavController().navigate(R.id.action_feed_to_services)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 