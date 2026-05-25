package com.example.healthtrack.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.healthtrack.R
import com.example.healthtrack.domain.model.AuthField
import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.databinding.FragmentLoginBinding
import com.example.healthtrack.ui.auth.register.RegisterFragment
import com.example.healthtrack.ui.dashboard.DashboardActivity

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        listeners()
    }

    private fun setupObservers() {
        viewModel.authStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    clearErrors()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is AuthResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    handleError(result)
                }
            }
        }
    }

    private fun handleError(error: AuthResult.Error) {
        when (error.field) {
            AuthField.EMAIL -> binding.tilEmail.error = error.message
            AuthField.PASSWORD -> binding.tilPassword.error = error.message
            else -> Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }

    private fun listeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.tvSignUp.setOnClickListener {
            parentFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                setReorderingAllowed(true)
                replace(R.id.fragment_container, RegisterFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
