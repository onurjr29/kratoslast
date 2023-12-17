package com.example.project00

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project00.databinding.FragmentHomeBinding
import com.example.project00.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val client = OkHttpClient()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAndSetPointFromFirebase()
        var chatResponse: String? = null
        binding.newBtn.setOnClickListener {
            getResponse() { response ->
                activity?.runOnUiThread {
                    binding.textBox.text = response
                }
            }
        }
        binding.checktBtn.setOnClickListener {
            checkAnswer() {Unit
                if(it == "True"){
                    updatePoint()
                    fetchAndSetPointFromFirebase()
                }
                binding.textBox.text = it
            }
        }
    }

    private fun getResponse(callback: (String) -> Unit){
        val apiKey=""
        val url = "https://api.openai.com/v1/engines/text-davinci-003/completions"

        val randomWord = generateRandomWord()
        val jsonObject = JSONObject()
        val question = "Bir cumle yaz. anahtar kelimesi de $randomWord olsun"
        binding.textBox.text = null
        jsonObject.put("prompt", question)
        jsonObject.put("max_tokens", 100)
        jsonObject.put("temperature", 0)

        val requestBody = jsonObject.toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject= JSONObject(body)
                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }
    private fun checkAnswer(callback: (String) -> Unit){
        val apiKey=""
        val url = "https://api.openai.com/v1/engines/text-davinci-003/completions"

        val jsonObject = JSONObject()
        val inputText = binding.inputBox.text.toString()
        val text = binding.textBox.text.toString()
        jsonObject.put("prompt", "is $inputText mean equal to $text ? say true or false")
        jsonObject.put("max_tokens", 100)
        jsonObject.put("temperature", 0)

        val requestBody = jsonObject.toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject= JSONObject(body)
                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }

    private fun generateRandomWord(): String {
        val words = listOf("ev","araba","masa", "bilgisayar", "kitap","kalem","cep telefonu",
            "ağaç","kuş","gökyüzü","deniz","güneş","ay","yıldız","otomobil","bisiklet","tren",
            "otobüs","uçak","balık","kedi","köpek","çiçek","meyve","sebze","su","ateş","toprak",
            "rüzgar","kar","yağmur","bulut","dağ","nehir","göl","köy","şehir","park","okul",
            "hastahane","restoran","market","film","müzik","resim","dans","spor","tatil","arkadaş",
            "aile","aşk")
        return words.random()
    }

    private fun fetchAndSetPointFromFirebase(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.child("point").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val point = dataSnapshot.getValue(Long::class.java)
                    binding.pointView.text = "Point: ${point.toString()}"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", "Veritabanı hatası: ${databaseError.message}")
                }
            })
        } else {
            Log.e("TAG", "Kullanıcı oturum açmamış.")
        }

    }

    private fun updatePoint(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId != null){
            val userReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userReference.child("3").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentPoint = snapshot.getValue(Long::class.java) ?: 0
                    val updatedPoint = currentPoint + 10

                    userReference.child("point").setValue(updatedPoint)
                        .addOnSuccessListener {
                            Log.d(TAG, "Puan güncellendi. Yeni puan: $updatedPoint")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Puan güncelleme hatası: $e")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Puan okuma hatası: $error")
                }
            })
        }
    }
}


