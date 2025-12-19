package com.example.myapplication.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Audio(
    val id: Long,
    val title: String?,
    val artist: String?,
    val data: String,
    val albumId: Long
) : Parcelable
