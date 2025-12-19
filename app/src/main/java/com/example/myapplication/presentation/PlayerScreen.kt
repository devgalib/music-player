package com.example.myapplication.presentation

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.data.Audio
import kotlinx.coroutines.delay
import kotlinx.serialization.Contextual
import kotlin.random.Random
import kotlin.random.nextInt
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer


fun getEmbeddedArt(context: Context, audioPath: String): ByteArray? {
    return try {
        MediaMetadataRetriever().apply {
            setDataSource(context, Uri.parse(audioPath))
        }.embeddedPicture
    } catch (e: Exception) {
        null
    }
}


@Composable
fun PlayerScreen (
    audioList:  List<@Contextual Audio>,
    initialIndex: Int = 0,
    onBack: () -> Unit
){
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var currentIndex by rememberSaveable { mutableStateOf(initialIndex) }
    var isShuffle by rememberSaveable { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isRepeat by rememberSaveable { mutableStateOf(false) }
    var elapsed by rememberSaveable { mutableStateOf(0L) }
    var duration by rememberSaveable { mutableStateOf(0L) }
    var shuffledList by rememberSaveable { mutableStateOf(audioList) }

    val waveform = remember { getWaveform() }
    var waveformProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(currentIndex, isShuffle) {
        val list = if (isShuffle) shuffledList else audioList
        val audio = list.getOrNull(currentIndex) ?: return@LaunchedEffect

        exoPlayer.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(audio.data))
            prepare()
            playWhenReady = true
        }
    }


    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener{
            override fun onIsPlayingChanged(isPlay: Boolean) {
                isPlaying = isPlay
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if(playbackState == Player.STATE_READY){
                    duration = exoPlayer.duration
                }
                if(playbackState == Player.STATE_ENDED){
                    currentIndex = (currentIndex + 1) % (if(isShuffle) shuffledList.size else audioList.size)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying){
            elapsed = exoPlayer.currentPosition
            waveformProgress = if (duration > 0) elapsed.toFloat() / duration else 0f
            delay(500)
        }
    }

    val audio = (if (isShuffle) shuffledList else audioList)
        .getOrNull(currentIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                listOf(
                    Color(0xff191c1f),
                    Color(0xff2c2c38)
                )
            ))
    ) {
        audio?.let { it ->

            val backgroundBitmap = remember(audio?.data) {
                audio?.data
                    ?.let { getEmbeddedArt(context, it) }
                    ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            }

            Box(modifier = Modifier.fillMaxSize()) {

                if (backgroundBitmap != null) {
                    Image(
                        bitmap = backgroundBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                if (android.os.Build.VERSION.SDK_INT >= 31) {
                                    renderEffect = RenderEffect
                                        .createBlurEffect(
                                            40f,
                                            40f,
                                            Shader.TileMode.CLAMP
                                        )
                                        .asComposeRenderEffect()
                                }
                            },
                        contentScale = ContentScale.Crop,
                        alpha = 0.40f
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.outline_music_note_24),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.40f
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 48.dp)
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0x30ffffff), shape = CircleShape),
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = audio?.let {
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            it.albumId,
                        )} ?: R.drawable.outline_music_note_24,
                    contentDescription = it.title,
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape)
                        .background(Color(0x30ffffff), shape = CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.outline_music_note_24),
                    placeholder = painterResource(id = R.drawable.outline_music_note_24)
                )
                Text(
                    text = audio?.title.orEmpty(),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 32.dp)
                )
                Text(
                    text = audio?.title.orEmpty(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .padding(top = 320.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Waveform(
                    values = waveform,
                    progress = waveformProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ){ percent ->
                    val seek = (percent * duration).toLong()
                    exoPlayer.seekTo(seek)
                    elapsed = seek
                    waveformProgress = percent
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime((elapsed/1000).toInt()),
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = formatTime((duration/1000).toInt()),
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                /**
                 * Repeat Button
                 */
                IconButton(
                    onClick = {
                        isRepeat = !isRepeat
                        exoPlayer.repeatMode = if(isRepeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_repeat_one_24),
                        contentDescription = null,
                        tint = if(isRepeat) Color(0xff9c27b0) else Color.White
                    )
                }
                /**
                 * Previous Button
                 */
                IconButton(
                    onClick = {
                        val list = if(isShuffle) shuffledList else audioList
                        currentIndex = if(currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_skip_previous_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                /**
                 * Play & Pause Button
                 */
                IconButton(
                    onClick = {
                        if(exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if(isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                        ),
                        contentDescription = null,
                        tint = Color(0xff1a1a1a)
                    )
                }
                /**
                 * Next Button
                 */
                IconButton(
                    onClick = {
                        val list = if(isShuffle) shuffledList else audioList
                        currentIndex = (currentIndex + 1) % list.size
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_skip_next_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                /**
                 * Shuffle Button
                 */
                IconButton(
                    onClick = {
                        isShuffle = !isShuffle
                        shuffledList = if(isShuffle) audioList.shuffled() else audioList
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_shuffle_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

        }
    }

}


fun getWaveform(): IntArray {
    val random = Random(System.currentTimeMillis())
    return IntArray(50){5+random.nextInt(50)}
}
fun formatTime(seconds: Int): String = String.format("%02d:%02d", seconds / 60, seconds % 60)