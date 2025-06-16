package com.yourappname.petapp.presentation.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourappname.petapp.databinding.FragmentDocumentsBinding
import com.yourappname.petapp.data.firebase.FirebaseSource
import com.yourappname.petapp.domain.model.VeterinaryPassport
import kotlinx.coroutines.launch

class DocumentsFragment : Fragment() {
    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseSource: FirebaseSource
    private lateinit var adapter: VeterinaryPassportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseSource = (requireActivity().application as com.yourappname.petapp.App).firebaseSource
        setupRecyclerView()
        loadPassports()
    }

    private fun setupRecyclerView() {
        adapter = VeterinaryPassportAdapter()
        binding.rvDocuments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDocuments.adapter = adapter
    }

    private fun loadPassports() {
        val user = firebaseSource.getCurrentUser()
        if (user == null) {
            Toast.makeText(requireContext(), "Користувач не авторизований", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val result = firebaseSource.getVeterinaryPassportsByUser(user.displayName ?: "")
            if (result.isSuccess) {
                adapter.submitList(result.getOrNull() ?: emptyList())
            } else {
                Toast.makeText(requireContext(), "Не вдалося завантажити паспорти", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 