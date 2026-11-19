package com.exemplo.iptvplay.data

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val group: String,
    val streamUrl: String
)
