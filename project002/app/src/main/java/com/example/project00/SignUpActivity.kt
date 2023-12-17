package com.example.project00

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.project00.databinding.ActivitySignUpBinding
import com.example.project00.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val createButton = binding.createButton
        val backButton = binding.backButton

        backButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        createButton.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val pass = binding.userPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            saveUsername()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.w("exampleClass", "CreateWithEmail: failure", task.exception)
                        }
                    }
            } else {
                Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUsername() {
        val username = binding.userName.text.toString().trim()

        if (username.isNotEmpty()) {
            val userId = auth.currentUser?.uid
            val userRef = userId?.let { database.reference.child("users").child(it) }

            userRef?.let {
                it.child("username").setValue(username)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            it.child("point").setValue(0L)
                        } else {
                            task.exception?.message
                        }
                    }
            }
        }
        else {
            Toast.makeText(this, "username is empty", Toast.LENGTH_SHORT).show()
        }
    }
}
