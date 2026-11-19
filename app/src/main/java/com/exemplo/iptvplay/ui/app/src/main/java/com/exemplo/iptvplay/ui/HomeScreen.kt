package com.exemplo.iptvplay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exemplo.iptvplay.data.Channel

@Composable
fun HomeScreen(channels: List<Channel>, onPlay: (Channel) -> Unit) {
    val groups = remember(channels) {
        listOf("Todos") + channels.map { it.group }.distinct().sorted()
    }
    var selectedGroup by remember { mutableStateOf("Todos") }

    val visible = remember(selectedGroup, channels) {
        if (selectedGroup == "Todos") channels else channels.filter { it.group == selectedGroup }
    }

    Row(
        modifier = Modifier.fillMaxSize().background(Color(0xFF101010)).padding(32.dp)
    ) {
        LazyColumn(modifier = Modifier.width(220.dp)) {
            items(groups) { group ->
                Surface(
                    onClick = { selectedGroup = group },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = SurfaceDefaults.colors(
                        containerColor = if (group == selectedGroup) Color(0xFF2962FF) else Color(0xFF1E1E1E),
                        focusedContainerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text(
                        text = group,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        fontSize = 16.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.width(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(visible, key = { it.id }) { ch ->
                ChannelCard(ch, onPlay)
            }
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel, onPlay: (Channel) -> Unit) {
    Surface(
        onClick = { onPlay(channel) },
        modifier = Modifier.fillMaxWidth().height(140.dp),
        colors = SurfaceDefaults.colors(
            containerColor = Color(0xFF1E1E1E),
            focusedContainerColor = Color(0xFF263238)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                modifier = Modifier.height(80.dp).fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = channel.name,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
