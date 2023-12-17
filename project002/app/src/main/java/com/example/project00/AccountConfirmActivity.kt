package com.example.project00

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project00.databinding.FragmentAccountConfirmBinding
import com.google.firebase.auth.FirebaseAuth

class AccountConfirmActivity : AppCompatActivity() {

    private lateinit var binding: FragmentAccountConfirmBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAccountConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnDeleteAccount.setOnClickListener {
            confirmAndDelete()
        }

    }

    private fun confirmAndDelete(){
        binding.btnDeleteAccount.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){
                auth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful){
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.delete()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                        Toast.makeText(this,"Account deleted successfully.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this,"Somethings went wrong during deleting.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else{
                            Toast.makeText(this,"e-posta veya sifre yanlis", Toast.LENGTH_SHORT).show()
                            Log.w("SignInActivity", "signInWithEmail: failure",task.exception)
                        }
                    }
            }
            else{
                Toast.makeText(this,"e-posta ve sifre bos olamaz", Toast.LENGTH_SHORT).show()
            }
    }
}
}