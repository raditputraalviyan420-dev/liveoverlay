
package com.liveoverlay

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.roundToInt

private val Background = Color(0xFF0B0915)
private val Panel = Color(0xFF19152A)
private val Purple = Color(0xFF9B6DFF)
private val Blue = Color(0xFF536DFF)
private val White = Color.White
private val Gray = Color(0xFFAAA5BA)

data class SongRequest(
    val title: String,
    val artist: String,
    val requester: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Background,
                    surface = Panel,
                    primary = Purple,
                    secondary = Blue
                )
            ) {
                LiveOverlayApp()
            }
        }
    }
}

@Composable
fun LiveOverlayApp() {
    var page by remember { mutableStateOf("Home") }
    var backgroundImage by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        backgroundImage = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "LIVE OVERLAY",
                    color = Purple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    page,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
            }

            Text(
                "● OFFLINE",
                color = Color(0xFFFF7272),
                fontSize = 12.sp
            )
        }

        // CONTENT
        Box(modifier = Modifier.weight(1f)) {
            when (page) {
                "Home" -> HomeScreen(
                    onEditor = { page = "Editor" },
                    onMusic = { page = "Music" },
                    onDonate = { page = "Donate" }
                )

                "Editor" -> EditorScreen(
                    image = backgroundImage,
                    onPickImage = {
                        picker.launch("image/*")
                    }
                )

                "Music" -> MusicScreen()

                "Donate" -> DonateScreen()

                "Settings" -> SettingsScreen()
            }
        }

        // NAVIGATION
        NavigationBar(
            containerColor = Panel
        ) {
            listOf("Home", "Editor", "Music", "Settings").forEach { item ->
                NavigationBarItem(
                    selected = page == item,
                    onClick = { page = item },
                    icon = {
                        Text(
                            when (item) {
                                "Home" -> "⌂"
                                "Editor" -> "▧"
                                "Music" -> "♫"
                                else -> "⚙"
                            }
                        )
                    },
                    label = { Text(item) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Purple,
                        selectedTextColor = Purple,
                        indicatorColor = Purple.copy(alpha = 0.15f),
                        unselectedTextColor = Gray
                    )
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onEditor: () -> Unit,
    onMusic: () -> Unit,
    onDonate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            "Welcome, Streamer!",
            color = White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Manage your livestream experience.",
            color = Gray
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF30205A),
                            Color(0xFF171D43)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Text("STREAM STATUS", color = Gray)
            Spacer(Modifier.height(8.dp))
            Text(
                "Not Connected",
                color = White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "TikTok integration not connected",
                color = Gray,
                fontSize = 12.sp
            )
        }

        FeatureCard(
            "🎨 Overlay Editor",
            "Customize your livestream layout",
            onEditor
        )

        FeatureCard(
            "🎵 Song Request",
            "Manage requested songs",
            onMusic
        )

        FeatureCard(
            "🎁 Donate Alert",
            "Configure SociaBuzz alert",
            onDonate
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Panel,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                title,
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(description, color = Gray, fontSize = 13.sp)
        }
    }
}

@Composable
fun EditorScreen(
    image: Uri?,
    onPickImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "OVERLAY CANVAS",
            color = Purple,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Drag widgets to arrange your overlay.",
            color = Gray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(12.dp))

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF302050),
                            Color(0xFF100D20)
                        )
                    )
                )
                .border(
                    1.dp,
                    Purple.copy(alpha = 0.5f),
                    RoundedCornerShape(18.dp)
                )
        ) {
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = "Overlay background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            DraggableWidget("🔴 LIVE • 00:00", 12f, 12f)
            DraggableWidget("🎁 DONATE ALERT", 12f, 65f)
            DraggableWidget("🎵 SONG REQUEST", 12f, 118f)
            DraggableWidget("💬 LIVE CHAT", 12f, 171f)
            DraggableWidget("YOUR AVATAR", 12f, 224f)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            )
        ) {
            Text("＋ Add Background Image")
        }

        Text(
            "Geser widget untuk mengatur posisi.",
            color = Gray,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun DraggableWidget(
    label: String,
    startX: Float,
    startY: Float
) {
    var x by remember { mutableFloatStateOf(startX) }
    var y by remember { mutableFloatStateOf(startY) }

    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (x * density.density).roundToInt(),
                    (y * density.density).roundToInt()
                )
            }
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xDD211B3B))
            .border(
                1.dp,
                Purple,
                RoundedCornerShape(10.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    x += dragAmount.x / density.density
                    y += dragAmount.y / density.density
                }
            }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            color = White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MusicScreen() {
    val queue = remember {
        mutableStateListOf<SongRequest>()
    }

    var command by remember { mutableStateOf("") }
    var requester by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("Spotify") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "SONG REQUEST",
            color = Purple,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Request lagu untuk livestream.",
            color = Gray,
            fontSize = 13.sp
        )

        Text(
            "MUSIC SOURCE",
            color = Purple,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Spotify", "YouTube").forEach { item ->
                FilterChip(
                    selected = source == item,
                    onClick = { source = item },
                    label = { Text(item) }
                )
            }
        }

        Text(
            "Sumber terpilih: $source",
            color = Gray,
            fontSize = 12.sp
        )

        HorizontalDivider(color = Purple.copy(alpha = 0.3f))

        Text(
            "ADD SONG REQUEST",
            color = Purple,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = command,
            onValueChange = { command = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Perintah lagu") },
            placeholder = {
                Text("!play APT. - ROSÉ & Bruno Mars")
            },
            singleLine = true
        )

        OutlinedTextField(
            value = requester,
            onValueChange = { requester = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nama peminta") },
            singleLine = true
        )

        Button(
            onClick = {
                val text = command.trim()
                val request = text
                    .removePrefix("!play")
                    .trim()

                if (!text.startsWith("!play", true)) {
                    message = "Gunakan perintah !play"
                } else if (request.isBlank()) {
                    message = "Judul lagu belum diisi."
                } else {
                    val parts = request.split(" - ", limit = 2)

                    queue.add(
                        SongRequest(
                            title = parts[0].trim(),
                            artist = if (parts.size > 1) {
                                parts[1].trim()
                            } else {
                                "Belum diketahui"
                            },
                            requester = requester.ifBlank {
                                "Anonymous"
                            }
                        )
                    )

                    command = ""
                    requester = ""
                    message = "Lagu berhasil ditambahkan!"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            )
        ) {
            Text("＋ Add to Queue")
        }

        Text(
            message,
            color = if (message.contains("berhasil")) {
                Color(0xFF72E6A0)
            } else {
                Color(0xFFFF8585)
            },
            fontSize = 12.sp
        )

        HorizontalDivider(color = Purple.copy(alpha = 0.3f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "PLAYLIST QUEUE",
                color = Purple,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text("${queue.size} songs", color = Gray)
        }

        if (queue.isEmpty()) {
            Text(
                "Belum ada lagu yang diminta.",
                color = Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        queue.forEachIndexed { index, song ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "${index + 1}. ${song.title}",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        song.artist,
                        color = Purple,
                        fontSize = 13.sp
                    )

                    Text(
                        "Requested by: ${song.requester}",
                        color = Gray,
                        fontSize = 11.sp
                    )

                    TextButton(
                        onClick = { queue.removeAt(index) }
                    ) {
                        Text(
                            "Remove",
                            color = Color(0xFFFF7272)
                        )
                    }
                }
            }
        }

        if (queue.isNotEmpty()) {
            OutlinedButton(
                onClick = { queue.removeAt(0) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏭ Skip First Song")
            }

            OutlinedButton(
                onClick = { queue.clear() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Queue")
            }
        }

        Text(
            "DEMO MODE: Request masih manual. " +
                "TikTok comments, Spotify API, YouTube search, " +
                "dan audio player belum terhubung.",
            color = Gray,
            fontSize = 11.sp
        )
    }
}

@Composable
fun DonateScreen() {
    var url by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "DONATE ALERT",
            color = Purple,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Hubungkan overlay SociaBuzz milikmu.",
            color = Gray
        )

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("SociaBuzz Overlay URL") },
            placeholder = { Text("https://...") },
            singleLine = true
        )

        Text(
            "Tempelkan URL Overlay Alert dari SociaBuzz, " +
                "bukan link halaman donasi.",
            color = Gray,
            fontSize = 12.sp
        )

        Text(
            "Status: Belum terhubung",
            color = Color(0xFFFF7272)
        )

        Text(
            "Penyimpanan dan pemuatan overlay akan ditambahkan " +
                "pada tahap integrasi berikutnya.",
            color = Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "SETTINGS",
            color = Purple,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Application: LiveOverlay", color = White)
        Text("Version: 0.2.0", color = Gray)
        Text("Theme: Dark Purple", color = Gray)

        HorizontalDivider(color = Purple.copy(alpha = 0.3f))

        Text(
            "Features",
            color = Purple,
            fontWeight = FontWeight.Bold
        )

        Text("• Overlay Editor", color = White)
        Text("• Song Request Queue", color = White)
        Text("• Donate Alert Setup", color = White)

        Text(
            "External integrations are not connected yet.",
            color = Gray,
            fontSize = 12.sp
        )
    }
}
