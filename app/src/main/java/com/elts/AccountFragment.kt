package com.elts

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.elts.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentUser == null) {
            binding.usernameText.text = "No user"
            binding.emailText.text = "-"
            return
        }

        // Fetch username and email from Firestore
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    binding.usernameText.text = doc.getString("username") ?: "User"
                    binding.emailText.text = doc.getString("email") ?: currentUser.email
                } else {
                    binding.usernameText.text = currentUser.displayName ?: "User"
                    binding.emailText.text = currentUser.email
                }
            }
            .addOnFailureListener {
                binding.usernameText.text = currentUser.displayName ?: "User"
                binding.emailText.text = currentUser.email
            }

        // inside onViewCreated after fetching user info
        binding.editUsernameIcon.setOnClickListener {
            showUpdateUsernameDialog()
        }

        // Logout button
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            // Navigate to login activity
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // New function to show dialog
    private fun showUpdateUsernameDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter new username"
        editText.setText(binding.usernameText.text)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Username")
            .setView(editText)
            .setPositiveButton("Update") { dialog, _ ->
                val newUsername = editText.text.toString().trim()
                if (newUsername.isNotEmpty() && currentUser != null) {
                    db.collection("users").document(currentUser.uid)
                        .update("username", newUsername)
                        .addOnSuccessListener {
                            binding.usernameText.text = newUsername
                            dialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to update username", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
