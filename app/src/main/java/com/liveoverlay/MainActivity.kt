package com.liveoverlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import android.net.Uri
import kotlin.math.roundToInt

private val Background = Color(0xFF0B0915)
private val Panel = Color(0xFF19152A)
private val Purple = Color(0xFF9B6DFF)
private val Blue = Color(0xFF536DFF)

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
    var currentPage by remember { mutableStateOf("Home") }
    var backgroundImage by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        backgroundImage = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    currentPage,
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text("● OFFLINE", color = Color(0xFFFF7272), fontSize = 12.sp)
        }

        // Main content
        Box(modifier = Modifier.weight(1f)) {
            when (currentPage) {
                "Home" -> HomeScreen(
                    onEditor = { currentPage = "Editor" },
                    onMusic = { currentPage = "Music" }
                )

                "Editor" -> EditorScreen(
                    image = backgroundImage,
                    onPickImage = { imagePicker.launch("image/*") }
                )

                "Music" -> MusicScreen()

                "Settings" -> SettingsScreen()
            }
        }

        // Bottom navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Panel)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavigationItem("Home", currentPage) {
                currentPage = "Home"
            }
            NavigationItem("Editor", currentPage) {
                currentPage = "Editor"
            }
            NavigationItem("Music", currentPage) {
                currentPage = "Music"
            }
            NavigationItem("Settings", currentPage) {
                currentPage = "Settings"
            }
        }
    }
}

@Composable
fun NavigationItem(
    title: String,
    selected: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            title,
            color = if (selected == title) Purple else Color.Gray,
            fontWeight = if (selected == title) FontWeight.Bold
            else FontWeight.Normal
        )
    }
}

@Composable
fun HomeScreen(
    onEditor: () -> Unit,
    onMusic: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Welcome, Streamer!",
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Manage your livestream experience.",
            color = Color.Gray
        )

        // Status card
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
            Text("STREAM STATUS", color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Not Connected",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
            Text("TikTok integration coming later", color = Color.Gray)
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
            "Customize donation notifications"
        ) {}
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
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(description, color = Color.Gray, fontSize = 13.sp)
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
            "Drag widgets to change their positions.",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

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

            // Decorative widgets
            DraggableWidget("LIVE • 00:00", 12f, 12f)
            DraggableWidget("🎁 DONATE ALERT", 12f, 65f)
            DraggableWidget("🎵 SONG REQUEST", 12f, 118f)
            DraggableWidget("💬 LIVE CHAT", 12f, 171f)
            DraggableWidget("YOUR AVATAR", 12f, 224f)
        }

        Spacer(modifier = Modifier.height(12.dp))

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
            "Tip: Geser widget untuk mengatur layout.",
            color = Color.Gray,
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
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MusicScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "SONG REQUEST",
            color = Purple,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Belum ada lagu yang diminta.",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Song request management will be available soon.",
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "SETTINGS",
            color = Purple,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Application: LiveOverlay", color = Color.White)
        Text("Version: 0.1.0", color = Color.Gray)
        Text("Theme: Dark Purple", color = Color.Gray)
    }
}