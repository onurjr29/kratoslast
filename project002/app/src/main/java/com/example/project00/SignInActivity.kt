package com.example.project00

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.project00.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val logInButton = binding.logInButton
        val createText = binding.createText
        createText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        logInButton.setOnClickListener {
            val email = binding.emailEdittext.text.toString()
            val pass = binding.passwordEdittext.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()){
                auth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful){
                            Log.d("signInActivity", "signInEmail: success")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,"e-posta veya sifre yanlis",Toast.LENGTH_SHORT).show()
                            Log.w("SignInActivity", "signInWithEmail: failure",task.exception)
                        }
                    }
            }
            else{
                Toast.makeText(this,"e-posta ve sifre bos olamaz",Toast.LENGTH_SHORT).show()
            }
        }
    }

}