package com.example.healthtrack.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtrack.domain.model.AuthField
import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.databinding.FragmentRegisterBinding
import com.example.healthtrack.R
import com.example.healthtrack.ui.dashboard.DashboardActivity

class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.authStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    clearErrors()
                    binding.progressBarRegister.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is AuthResult.Success -> {
                    binding.progressBarRegister.visibility = View.GONE
                    Toast.makeText(requireContext(), "Cuenta creada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is AuthResult.Error -> {
                    binding.progressBarRegister.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    handleError(result)
                }
            }
        }
    }

    private fun handleError(error: AuthResult.Error) {
        when (error.field) {
            AuthField.EMAIL -> {
                binding.viewFlipperRegister.displayedChild = 0
                binding.tilEmailRegister.error = error.message
            }
            AuthField.PASSWORD -> {
                binding.viewFlipperRegister.displayedChild = 0
                binding.tilPasswordRegister.error = error.message
            }
            AuthField.CONFIRM_PASSWORD -> {
                binding.viewFlipperRegister.displayedChild = 0
                binding.tilConfirmPassword.error = error.message
            }
            AuthField.FULL_NAME -> {
                binding.viewFlipperRegister.displayedChild = 1
                binding.tilName.error = error.message
            }
            AuthField.USERNAME -> {
                binding.viewFlipperRegister.displayedChild = 1
                binding.tilUsername.error = error.message
            }
            AuthField.NONE -> Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun clearErrors() {
        binding.tilName.error = null
        binding.tilEmailRegister.error = null
        binding.tilPasswordRegister.error = null
        binding.tilConfirmPassword.error = null
        binding.tilUsername.error = null
    }

    private fun setupListeners(){
        binding.btnNextStep.setOnClickListener {
            if (validateFirstStep()) {
                binding.viewFlipperRegister.setInAnimation(requireContext(), R.anim.slide_in_right)
                binding.viewFlipperRegister.setOutAnimation(requireContext(), R.anim.slide_out_left)
                binding.viewFlipperRegister.displayedChild = 1
            }
        }

        binding.tvBackStep.setOnClickListener {
            binding.viewFlipperRegister.setInAnimation(requireContext(), R.anim.slide_in_left)
            binding.viewFlipperRegister.setOutAnimation(requireContext(), R.anim.slide_out_right)
            binding.viewFlipperRegister.displayedChild = 0
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val fullName = binding.etName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()

            viewModel.register(email, password, confirmPassword, fullName, username)
        }

        binding.tvBackToLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun validateFirstStep(): Boolean {
        val email = binding.etEmailRegister.text.toString().trim()
        val password = binding.etPasswordRegister.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        var isValid = true
        if (email.isEmpty()) {
            binding.tilEmailRegister.error = "Ingresa un correo"
            isValid = false
        } else {
            binding.tilEmailRegister.error = null
        }

        if (password.length < 8) {
            binding.tilPasswordRegister.error = "La contraseña debe tener al menos 8 caracteres"
            isValid = false
        } else {
            binding.tilPasswordRegister.error = null
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
