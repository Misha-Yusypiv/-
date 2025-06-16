package com.yourappname.petapp.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.yourappname.petapp.R
import com.yourappname.petapp.data.firebase.FirebaseSource
import com.yourappname.petapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var firebaseSource: FirebaseSource
    private lateinit var googleSignInClient: GoogleSignInClient
    
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseSource = (requireActivity().application as com.yourappname.petapp.App).firebaseSource
        
        // Initialize Google Sign-In
        googleSignInClient = firebaseSource.getGoogleSignInClient(getString(R.string.default_web_client_id))
        
        binding.btnGoogleSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                data?.let { intent ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.progressBar.visibility = View.VISIBLE
                        firebaseSource.signInWithGoogle(intent)
                            .onSuccess {
                                findNavController().navigate(R.id.action_login_to_feed)
                            }
                            .onFailure { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "Authentication failed: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        binding.progressBar.visibility = View.GONE
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google sign in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 