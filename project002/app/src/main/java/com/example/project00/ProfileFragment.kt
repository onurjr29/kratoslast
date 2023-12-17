package com.example.project00

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.project00.UserListAdapter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.project00.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAndSetUsernameFromFirebase()
        fetchAndSetPointFromFirebase()

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

        val userList = listOf(
            User("Kullanıcı1", 100),
            User("Kullanıcı2", 150)
        )

        val adapter = UserListAdapter(requireContext(), userList)

        binding.listViewUsers.adapter = adapter
    }

    private fun fetchAndSetUsernameFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.child("username").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.getValue(String::class.java)
                    if (username != null) {
                        binding.userName.text = username
                    } else {
                        Log.e("TAG", "Kullanıcı adı bulunamadı.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", "Veritabanı hatası: ${databaseError.message}")
                }
            })
        } else {
            Log.e("TAG", "Kullanıcı oturum açmamış.")
        }
    }

    private fun fetchAndSetPointFromFirebase(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.child("point").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val point = dataSnapshot.getValue(Int::class.java)
                    binding.pointProfile.text = "Point: ${point.toString()}"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", "Veritabanı hatası: ${databaseError.message}")
                }
            })
        } else {
            Log.e("TAG", "Kullanıcı oturum açmamış.")
        }

    }
}
