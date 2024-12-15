package com.example.newsprojectpractice.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentRegisterBinding
import com.example.newsprojectpractice.ui.NewsActivity
import com.example.newsprojectpractice.util.UserPreferenceManager

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var userPreferenceManager: UserPreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        userPreferenceManager = UserPreferenceManager.getInstance(requireContext())

        with(binding) {
            RegisterButton.setOnClickListener {
                val usernameInput = usernameInput.text.toString()
                val passwordInput = passwordInput.text.toString()
                val confirmPasswordInput = confirmPasswordInput.text.toString()

                if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
                    Toast.makeText(requireContext(), "Mohon isi semua data", Toast.LENGTH_SHORT).show()
                } else if (passwordInput != confirmPasswordInput) {
                    Toast.makeText(requireContext(), "Password tidak sama", Toast.LENGTH_SHORT).show()
                } else {
                    userPreferenceManager.saveUserName(usernameInput)
                    userPreferenceManager.saveUserPassword(passwordInput)
                    userPreferenceManager.setUserLoggedIn(true)
                    checkRegistrationStatus()
                }
            }

            loginText.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun checkRegistrationStatus() {
        if (userPreferenceManager.isUserLoggedIn()) {
            Toast.makeText(requireContext(), "Registrasi berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), NewsActivity::class.java))
            requireActivity().finish()
        } else {
            Toast.makeText(requireContext(), "Registrasi gagal", Toast.LENGTH_SHORT).show()
        }
    }
}
