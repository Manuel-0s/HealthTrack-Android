package com.example.healthtrack.ui.profile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtrack.R
import com.example.healthtrack.databinding.FragmentProfileBinding
import com.example.healthtrack.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelObservers()
        viewModel.loadUserData()
        setupClickListeners()
    }

    private fun setupViewModelObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.nombre
                binding.tvUserEmail.text = it.correo

                binding.tvUserBirthDate.text = it.fechaNacimiento.ifEmpty { "No asignada" }
                binding.tvUserHeight.text = "${it.height} cm"
                binding.tvUserWeight.text = "${it.weight} kg"
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.loggedOut.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnEditName.setOnClickListener {
            showEditDialog(
                title = "Modificar Nombre",
                hint = "Nombre completo",
                currentValue = binding.tvUserName.text.toString(),
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ) { nuevoNombre ->
                binding.tvUserName.text = nuevoNombre
                viewModel.updateUserName(nuevoNombre)
            }
        }
        binding.btnEditEmail.setOnClickListener {
            showEditDialog(
                title = "Modificar Correo",
                hint = "Dirección de correo",
                currentValue = binding.tvUserEmail.text.toString(),
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            ) { nuevoEmail ->
                binding.tvUserEmail.text = nuevoEmail
                viewModel.updateUserEmail(nuevoEmail)
            }
        }

        binding.btnEditBirthDate.setOnClickListener {
            showEditDialog(
                title = "Modificar Fecha",
                hint = "Fecha de nacimiento",
                currentValue = binding.tvUserBirthDate.text.toString(),
                inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
            ) { nuevaFecha ->
                binding.tvUserBirthDate.text = nuevaFecha
                viewModel.updateBirthDate(nuevaFecha)
            }
        }

        binding.btnEditHeight.setOnClickListener {
            val cleanHeight = binding.tvUserHeight.text.toString().replace(" cm", "")
            showEditDialog(
                title = "Modificar Estatura",
                hint = "Estatura (cm)",
                currentValue = cleanHeight,
                inputType = InputType.TYPE_CLASS_NUMBER
            ) { nuevaEstatura ->
                binding.tvUserHeight.text = "$nuevaEstatura cm"
                viewModel.updateHeight(nuevaEstatura.toIntOrNull() ?: 0)
            }
        }

        binding.btnEditWeight.setOnClickListener {
            val cleanWeight = binding.tvUserWeight.text.toString().replace(" kg", "")
            showEditDialog(
                title = "Modificar Peso",
                hint = "Peso (kg)",
                currentValue = cleanWeight,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            ) { nuevoPeso ->
                binding.tvUserWeight.text = "$nuevoPeso kg"
                viewModel.updateWeight(nuevoPeso.toDoubleOrNull() ?: 0.0)
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun showEditDialog(
        title: String,
        hint: String,
        currentValue: String,
        inputType: Int,
        onConfirm: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_text, null)
        val tilGeneric = dialogView.findViewById<TextInputLayout>(R.id.tilGeneric)
        val etGenericValue = dialogView.findViewById<EditText>(R.id.etGenericValue)

        tilGeneric.hint = hint
        etGenericValue.setText(currentValue)
        etGenericValue.inputType = inputType
        etGenericValue.setSelection(currentValue.length)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Actualizar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newValue = etGenericValue.text.toString().trim()
            if (newValue.isNotEmpty()) {
                onConfirm(newValue)
                dialog.dismiss()
            } else {
                tilGeneric.error = "Este campo no puede estar vacío"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}