package com.example.newsprojectpractice.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentLoginBinding
import com.example.newsprojectpractice.ui.NewsActivity
import com.example.newsprojectpractice.util.UserPreferenceManager

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var userPreferenceManager: UserPreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        userPreferenceManager = UserPreferenceManager.getInstance(requireContext())

        // Mengecek status login di awal
        if (userPreferenceManager.isUserLoggedIn()) {
            navigateToNewsActivity()
        }

        with(binding) {
            loginButton.setOnClickListener {
                val usernameInput = usernameInput.text.toString()
                val passwordInput = passwordInput.text.toString()

                if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(requireContext(), "Mohon isi semua data", Toast.LENGTH_SHORT).show()
                } else {
                    if (isValidUserLogin(usernameInput, passwordInput)) {
                        // Menyimpan status login pengguna
                        userPreferenceManager.setUserLoggedIn(true)
                        userPreferenceManager.saveUserName(usernameInput) // Menyimpan username jika login sukses
                        userPreferenceManager.saveUserPassword(passwordInput) // Menyimpan password jika login sukses
                        checkLoginStatus()
                    } else {
                        Toast.makeText(requireContext(), "Username atau password salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            registerText.setOnClickListener {
                // Arahkan ke Fragment Register
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun isValidUserLogin(inputUsername: String, inputPassword: String): Boolean {
        val storedUsername = userPreferenceManager.getUserName()
        val storedPassword = userPreferenceManager.getUserPassword()

        // Validasi username dan password
        return storedUsername == inputUsername && storedPassword == inputPassword
    }

    private fun checkLoginStatus() {
        if (userPreferenceManager.isUserLoggedIn()) {
            // Login berhasil, buka NewsActivity
            navigateToNewsActivity()
        } else {
            // Login gagal, tampilkan pesan
            Toast.makeText(requireContext(), "Login gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNewsActivity() {
        // Memulai NewsActivity setelah login sukses
        startActivity(Intent(requireContext(), NewsActivity::class.java))
        requireActivity().finish() // Menutup activity login agar tidak bisa kembali ke login
    }
}
