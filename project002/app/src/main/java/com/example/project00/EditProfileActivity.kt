package com.example.project00

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.project00.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var btnSave: Button
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        editTextUsername = binding.editTextNewUsername
        btnSave = binding.btnSaveUsername

        btnSave.setOnClickListener {
            saveUsername()
        }

        binding.btnSavePass.setOnClickListener {
            editPass()
        }
    }

    private fun saveUsername() {
        val newUsername = editTextUsername.text.toString().trim()

        if (newUsername.isNotEmpty()) {
            val userId = auth.currentUser?.uid
            val userRef = userId?.let { database.reference.child("users").child(it) }

            userRef?.child("username")?.setValue(newUsername)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Username saved succesfully.", Toast.LENGTH_SHORT).show();
                        finish()
                    } else {
                        Toast.makeText(this, "Somethings went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
        } else {
            Toast.makeText(this, "Username box is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private fun editPass(){
        val currentPassword = binding.editCurrentPass.text.toString()
        val newPassword = binding.editNewPass.text.toString()

        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)


        user?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        when (updateTask.exception) {
                            is FirebaseAuthWeakPasswordException -> {
                                Toast.makeText(this, "Weak password. Choose a stronger one.", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthRecentLoginRequiredException -> {
                                Toast.makeText(this, "Please sign in again to change your password", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Password change failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
