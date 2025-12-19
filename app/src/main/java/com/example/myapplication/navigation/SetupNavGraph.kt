package com.example.myapplication.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.myapplication.presentation.AudioListScreen
import com.example.myapplication.presentation.PlayerActivity
import com.example.myapplication.presentation.PlayerScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Screen,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AudiosList//JuzScreen(TrackerType.JUZ,"0", 1,5,2,7)//MemorizationSurah("fld_1764446507046_631ebc75")
    ) {
        composable<Screen.AudiosList> {
            AudioListScreen(onAudioClick = { audios, position ->
                val intent = Intent(navController.context, PlayerActivity::class.java)
                intent.putParcelableArrayListExtra("audios", ArrayList(audios))
                intent.putExtra("position", position)
                navController.context.startActivity(intent)
            })
        }
    }
}
