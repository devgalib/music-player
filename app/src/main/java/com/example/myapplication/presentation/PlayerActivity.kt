package com.example.myapplication.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.data.Audio

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myAudiosList = intent.getParcelableArrayListExtra("audios") ?: emptyList<Audio>()
        val myPosition = intent.getIntExtra("position", 0)

        enableEdgeToEdge()
        setContent {
            PlayerScreen(
                audioList = myAudiosList,
                initialIndex = myPosition,
                onBack = { finish() }
            )
        }
    }
}