package com.example.project00

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.project00.databinding.ActivityProfileSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveEmail.setOnClickListener {
            updateMail()
        }

        binding.btnDeleteAccount.setOnClickListener {
            val intent = Intent(this, AccountConfirmActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateMail() {
        val user = FirebaseAuth.getInstance().currentUser
        val newEmail = binding.editTextNewEmail.text.toString()

        user?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "E-posta güncelleme başarılı.")
                } else {
                    Log.e(TAG, "E-posta güncelleme başarısız.", task.exception)
                }
            }
    }
}