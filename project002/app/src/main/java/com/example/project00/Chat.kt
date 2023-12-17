package com.example.project00

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat")
data class Chat(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val userName: String,
    val text: String,
    var isSelf: Boolean = false
)